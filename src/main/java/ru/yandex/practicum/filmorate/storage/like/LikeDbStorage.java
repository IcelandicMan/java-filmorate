package ru.yandex.practicum.filmorate.storage.like;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        final String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Пользователь с id {} поставил лайк к фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        final String sql = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}", userId, filmId);
    }

    @Override
    public List<Film> getFilmsByLikes(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, " +
                "m.name AS mpa_name, " +
                "COUNT (fl.user_id) AS rate " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_likes AS fl ON f.id = fl.film_id ";
        String sqlEnd = "GROUP BY f.id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        List<Film> popularFilms;
        if (genreId == null && year == null) {
            sqlQuery += sqlEnd;
            popularFilms = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, count);
        } else if (year == null) {
            sqlQuery += "LEFT JOIN film_genres AS fg ON f.id = fg.film_id " +
                    "WHERE fg.genre_id = ? " +
                    sqlEnd;
            popularFilms = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, genreId, count);
        } else if (genreId == null) {
            sqlQuery += "WHERE EXTRACT (YEAR FROM CAST (f.releaseDate AS date)) = ? " +
                    sqlEnd;
            popularFilms = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, year, count);
        } else {
            sqlQuery += "LEFT JOIN film_genres AS fg ON f.id = fg.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "AND " +
                    "EXTRACT (YEAR FROM CAST (f.releaseDate AS date)) = ? " +
                    sqlEnd;
            popularFilms = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, genreId, year, count);
        }
        return popularFilms;
    }
}

