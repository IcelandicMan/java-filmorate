
package ru.yandex.practicum.filmorate.storage.film.like;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/*
@Component
@AllArgsConstructor
public class LikeDbStorage implements LikeStorage{

    private JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
    String sqlStatus = "SELECT film_id, user_id FROM user_friends WHERE user_id = ? AND friend_id = ?";
    List<Integer> StatusList = jdbcTemplate.query(sqlStatus, new Object[]{userId, userId}, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    int filmId = rs.getInt("film_id");
                    int userId = rs.getInt("user_id");
                    return null;
                }
            });
        }


    String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";


    @Override
    public void deleteLike(int filmId, int userId) {

    }


}
 */