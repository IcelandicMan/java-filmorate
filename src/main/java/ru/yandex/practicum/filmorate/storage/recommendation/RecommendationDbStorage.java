package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

@Component
@AllArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getRecommendation(int userId) {
        final String sql =
                //3. Запрос на предоставление фильмов по id из подзапроса 2
                "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, " +
                        "m.name AS mpa_name, " +
                        "COUNT (fl.user_id) AS rate " +
                        "FROM films AS f " +
                        "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                        "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                        "WHERE f.id IN " +
                        //2. Найдем все фильмы, которые были лайкнуты найденным пользователем, но...  см 2.1
                        "(SELECT DISTINCT  fl.film_id " +
                        "FROM film_likes AS fl " +
                        "WHERE fl.user_id IN " +
                        //1. Найдем пользователей, чьи лайки совпадают с лайками пользователя с id = ?:
                        "(SELECT DISTINCT fl2.user_id " +
                        "FROM film_likes AS fl1 " +
                        "JOIN film_likes AS fl2 ON fl1.film_id = fl2.film_id " +
                        "WHERE fl1.user_id = ?) " +
                        // 2.1 но не были лайкнуты пользователем с id = 1:
                        "AND fl.film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)) " +
                        // конец запроса
                        "GROUP BY f.id " +
                        "ORDER BY f.id";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm, userId, userId);
    }
}
