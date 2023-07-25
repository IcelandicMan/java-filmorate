package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("filmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        String sql = "INSERT INTO films (name, description, releaseDate, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
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
            film.setId(generatedId);
            saveDirector(film);
            return getFilm(generatedId);
        } else {
            throw new RuntimeException("Failed to retrieve generated keys");
        }
    }

    @Override
    public Film getFilm(int id) {
        log.info("Получение фильма с id {}", id);
        final String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, " +
            "m.name AS mpa_name, " +
            "COUNT (fl.user_id) AS rate " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id";
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
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, " +
            "m.name AS mpa_name, " +
            "COUNT (fl.user_id) AS rate " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
            "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
            "GROUP BY f.id " +
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
                "mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, updatedFilm.getName(), updatedFilm.getDescription(),
                java.sql.Date.valueOf(updatedFilm.getReleaseDate()), updatedFilm.getDuration(),
            updatedFilm.getMpa().getId(), updatedFilm.getId());
        saveGenres(updatedFilm);
        saveDirector(updatedFilm);
        log.info("Фильм под id {} обновлен: {} ", filmId, updatedFilm);
        return updatedFilm;
    }

    @Override
    public void deleteFilm(int id) {
        log.info("Удаление фильма с id {}", id);
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Фильм с id {} удален", id);
    }

    @Override
    public List<Film> getFilmsSortByYear(Integer id) {
        log.info("Получение списка всех фильмов режиссёра по годам");
        String sql = "SELECT f.ID, " +
                "f.NAME, " +
                "f.DESCRIPTION, " +
                "f.RELEASEDATE, " +
                "f.DURATION, " +
                "f.MPA_ID, " +
                "m.ID mpa_id, " +
                "m.NAME mpa_name, " +
                "g.ID genre_id, " +
                "g.NAME genre_name, " +
                "d.ID director_id, " +
                "d.NAME director " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
                "LEFT JOIN film_genres fg on f.id = fg.film_id " +
                "LEFT JOIN genres g on fg.GENRE_ID  = g.id " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID  = d.ID " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "WHERE d.ID = ? " +
                "ORDER BY f.RELEASEDATE ";
        List<Film> filmList = jdbcTemplate.query(sql, rowMapperFilm(), id);
        if (filmList.isEmpty()) {
            throw new DirectorNotFoundException("Режиссёр c id " + id + " не найден");
        }
        log.info("Список фильмов режиссёра с id = " + id + " получен");
        return filmList;
    }

    @Override
    public List<Film> getFilmsSortByLikes(Integer id) {
        log.info("Получение списка всех фильмов режиссёра по лайкам");
        String sql = "SELECT f.ID, " +
                "f.NAME, " +
                "f.DESCRIPTION, " +
                "f.RELEASEDATE, " +
                "f.DURATION, " +
                "f.MPA_ID";
        sql += ", m.ID mpa_id, " +
                "m.NAME mpa_name";
        sql += ", g.ID genre_id, " +
                "g.NAME genre_name";
        sql += ", d.ID director_id, " +
                "d.NAME director";
        sql +=  ", COUNT(fl.USER_ID) likes";
        sql +=  " FROM FILMS f ";
        sql += "LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
                "LEFT JOIN film_genres fg on f.id = fg.film_id " +
                "LEFT JOIN genres g on fg.GENRE_ID  = g.id " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID  = d.ID " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN FILM_LIKES fl ON fl.FILM_ID = f.ID ";
        sql += "WHERE d.ID = ? " +
                "GROUP BY f.ID " +
                "ORDER BY likes DESC ";
        List<Film> filmList = jdbcTemplate.query(sql, rowMapperFilm(), id);
        if (filmList.isEmpty()) {
            throw new DirectorNotFoundException("Режиссёр c id " + id + " не найден");
        }
        log.info("Список фильмов режиссёра с id = " + id + " получен");
        return filmList;
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("rate"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
       return film;
    }

    private void saveGenres(Film film) {
        final List<Genre> genres = film.getGenres();
        if (genres == null) {
            return;
        }
        final int filmId = film.getId();
        jdbcTemplate.update("DELETE from film_genres where film_id = ?", filmId);
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


    private void saveDirector(Film film) {
        final List<Director> directors = film.getDirectors();
        if (directors == null) {
            return;
        }
        final int filmId = film.getId();
        jdbcTemplate.update("DELETE from film_directors where film_id = ?", filmId);
        if (directors.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_directors (film_id, director_id) SELECT ?, ? " +
                        "WHERE NOT EXISTS (SELECT 1 FROM film_directors WHERE film_id = ? AND director_id = ?)",
                new BatchPreparedStatementSetter() {
                    final List<Director> directorList = new ArrayList<>(directors);

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Director director = directorList.get(i);
                        ps.setInt(1, filmId);
                        ps.setInt(2, director.getId());
                        ps.setInt(3, filmId);
                        ps.setInt(4, director.getId());
                    }

                    public int getBatchSize() {
                        return directorList.size();
                    }
                });
    }

    private RowMapper<Film> rowMapperFilm() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setReleaseDate(rs.getDate("releasedate").toLocalDate());
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            if (rs.getInt("genre_id") > 0) {
                film.setGenres(FilmDbStorage.this.newGenre(rs));
            } else film.setGenres(new ArrayList<>());
            film.setDirectors(List.of(new Director(rs.getInt("director_id"), rs.getString("director"))));
            return film;
        };
    }

    @SneakyThrows
    private List<Genre> newGenre(ResultSet rs) {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
        return genres;
    }
}
