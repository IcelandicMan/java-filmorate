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
                "m.name AS mpa_name, " +
                "d.ID director_id, " +
                "d.NAME director " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN FILM_DIRECTORS fd ON f.id = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID  = d.ID " +
                "ORDER by rate DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, count);
    }

}

