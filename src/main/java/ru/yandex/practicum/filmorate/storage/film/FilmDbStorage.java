package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private GenreStorage genreStorage;

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        String sql = "INSERT INTO films (film_name, film_description, film_releaseDate, film_duration, film_rate, film_mpa_id) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();
        if (!generatedKeys.isEmpty()) {
            Map<String, Object> keysMap = generatedKeys.get(0); // Предполагаем, что нужен первый сгенерированный ключ
            int generatedId = (Integer) keysMap.get("film_id");

            // Вставляем связи с жанрами
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", generatedId, genre.getId());
            }
            Film createdFilm = getFilm(generatedId);
            log.info("Фильм под id {} создан: {}", createdFilm.getId(), createdFilm);
            return createdFilm;
        } else {
            throw new RuntimeException("Failed to retrieve generated keys");
        }
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма под id {}: {}", updatedFilm.getId(), updatedFilm);
        final int filmId = updatedFilm.getId();
        String sql = "Update films SET " +
                "film_name = ?, " +
                "film_description = ?, " +
                "film_releaseDate = ?, " +
                "film_duration = ?, " +
                "film_rate = ?, " +
                "film_mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, updatedFilm.getName(), updatedFilm.getDescription(),
                java.sql.Date.valueOf(updatedFilm.getReleaseDate()), updatedFilm.getDuration(),
                updatedFilm.getRate(), updatedFilm.getMpa().getId(), updatedFilm.getId());

        // Обновляем связи с жанрами
        genreStorage.deleteGenresByFilmId(updatedFilm.getId());
        for (Genre genre : updatedFilm.getGenres()) {
            int genreId = genre.getId();
            if (!isGenreExistsForFilm(filmId, genreId)) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", updatedFilm.getId(), genre.getId());
            }
        }

        log.info("Фильм под id {} обновлен: {} ", updatedFilm.getId(), updatedFilm);
        return getFilm(updatedFilm.getId());
    }

    @Override
    public void deleteFilm(int id) {
        log.info("Удаление фильма с id {}", id);
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Фильм с id {} удален", id);
    }

    @Override
    public Film getFilm(int id) {
        log.info("Получение фильма с id {}", id);
        Film film = null;
        String sqlStatus = "SELECT film_id FROM films WHERE film_id = ?";
        List<Integer> filmIdList = jdbcTemplate.query(sqlStatus, new Object[]{id}, (rs, rowNum) -> rs.getInt("film_id"));

        if (filmIdList.isEmpty()) {
            log.error("Фильм с id {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм c id %s не найден", id));
        } else if (filmIdList.get(0) == id) {
            String sql = "SELECT f.film_id, " +
                    "f.film_name, " +
                    "f.film_description, " +
                    "f.film_releaseDate, " +
                    "f.film_duration, " +
                    "f.film_rate, " +
                    "f.film_mpa_id, " +
                    "m.mpa_name, " +
                    "fg.genre_id, " +
                    "g.genre_name " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa AS m ON f.film_mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres AS g ON fg.genre_id  = g.genre_id " +
                    "WHERE f.film_id = ?";
            film = jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
            log.info("Фильм с id {} получен: {}", id, film);
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.info("Получение списка всех Фильмов");
        String sql = "SELECT f.film_id, " +
                "f.film_name, " +
                "f.film_description, " +
                "f.film_releaseDate, " +
                "f.film_duration, " +
                "f.film_rate, " +
                "f.film_mpa_id, " +
                "m.mpa_name, " +
                "fg.genre_id, " +
                "g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.film_mpa_id = m.mpa_id " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id  = g.genre_id " +
                "ORDER BY f.film_id";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper());
        log.info("Список всех фильмов получен");
        return filmList;
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            int filmId = rs.getInt("film_id");
            Film film = new Film();
            film.setId(filmId);
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("film_description"));
            film.setReleaseDate(rs.getDate("film_releaseDate").toLocalDate());
            film.setDuration(rs.getInt("film_duration"));
            film.setRate(rs.getInt("film_rate"));

            int mpaId = rs.getInt("film_mpa_id");
            if (mpaId != 0) {
                Mpa mpa = new Mpa(mpaId, rs.getString("mpa_name"));
                film.setMpa(mpa);
            }

            List<Genre> genres = new ArrayList<>();
            do {
                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    Genre genre = new Genre(genreId, rs.getString("genre_name"));
                    genres.add(genre);
                }
            } while (rs.next() && filmId == rs.getInt("film_id"));

            film.setGenres(genres);
            return film;
        };
    }

    private boolean isGenreExistsForFilm(int filmId, int genreId) {
        String sql = "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, genreId);
        return count != null && count > 0;
    }
}
