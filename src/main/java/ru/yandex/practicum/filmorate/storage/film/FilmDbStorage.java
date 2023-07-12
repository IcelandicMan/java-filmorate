package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        String sql = "INSERT INTO films (name, description, releaseDate, duration, rate, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";

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
            int generatedId = (Integer) keysMap.get("id");


            // Вставляем связи с жанрами
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", generatedId, genre.getId());
            }
            return getFilm(generatedId);
        } else {
            throw new RuntimeException("Failed to retrieve generated keys");
        }
    }

    @Override
    public Film getFilm(int id) {
        log.info("Получение фильма с id {}", id);
        final String sqlQuery = "SELECT *, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, id);
        if (films.size() != 1) {
            log.error("Фильм с id {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм c id %s не найден", id));
        }
        log.info("Фильм с id {} получен: {}", id, films.get(0));
        return films.get(0);
    }

    @Override
    public List<Film> getFilms() {
        log.info("Получение списка всех Фильмов");
        String sql = "SELECT *, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "ORDER BY f.id";
        List<Film> filmList = jdbcTemplate.query(sql, FilmDbStorage::makeFilm);
        log.info("Список всех фильмов получен");
        return filmList;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма под id {}: {}", updatedFilm.getId(), updatedFilm);
        final int filmId = updatedFilm.getId();
        final String sql = "Update films SET " +
                "name = ?, " +
                "description = ?, " +
                "releaseDate = ?, " +
                "duration = ?, " +
                "rate = ?, " +
                "mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, updatedFilm.getName(), updatedFilm.getDescription(),
                java.sql.Date.valueOf(updatedFilm.getReleaseDate()), updatedFilm.getDuration(),
                updatedFilm.getRate(), updatedFilm.getMpa().getId(), updatedFilm.getId());
        saveGenres(updatedFilm);
        log.info("Фильм под id {} обновлен: {} ", updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @Override
    public void deleteFilm(int id) {
        log.info("Удаление фильма с id {}", id);
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Фильм с id {} удален", id);
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("rate"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }

    private void saveGenres(Film film) {
        final int filmId = film.getId();
        jdbcTemplate.update("DELETE from film_genres where film_id = ?", filmId);
        final List<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM film_genres WHERE film_id = ? AND genre_id = ?)",
                new BatchPreparedStatementSetter() {
                    List<Genre> genreList = new ArrayList<>(genres);

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Genre genre = genreList.get(i);
                        ps.setInt(1, filmId);
                        ps.setInt(2, genre.getId());
                        ps.setInt(3, filmId);
                        ps.setInt(4, genre.getId());
                    }

                    public int getBatchSize() {
                        return genreList.size();
                    }
                });
    }
}
