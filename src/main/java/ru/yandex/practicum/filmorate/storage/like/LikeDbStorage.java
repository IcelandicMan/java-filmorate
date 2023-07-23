package ru.yandex.practicum.filmorate.storage.like;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        updateRate(filmId);
    }

    @Override
    public void updateRate(int filmId) {
        final String updateRate =
                "UPDATE films AS f SET rate = rate + " +
                        "(SELECT COUNT (l.user_id) FROM film_likes AS l WHERE f.id = l.film_id) " +
                        "WHERE id = ?";
        jdbcTemplate.update(updateRate, filmId);
        log.info("Рейтинг фильма с id {} изменен", filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        final String sql = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}", userId, filmId);
        updateRate(filmId);
    }

    @Override
    public List<Film> getFilmsByLikes(int count) {
        final String sqlQuery = "SELECT *, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "ORDER by rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, count);
    }

    @Override
    public List<Film> getCommonUsersFilms(int userId, int friendId) {
        final String sqlQuery = "SELECT DISTINCT *, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "INNER JOIN film_likes AS fl on f.ID = fl.film_id " +
                "WHERE fl.user_id IN (?, ?) " +
                "ORDER by rate DESC;";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, userId, friendId).stream()
                .distinct()
                .collect(Collectors.toList());
    }
}

