package ru.yandex.practicum.filmorate.storage.user.friend;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
@AllArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlAddFriendRequest;
        String sqlIncomingRequest;
        String sqlStatus = "SELECT status_id FROM user_friends WHERE user_id = ? AND friend_id = ?";

        List<Integer> statusList = jdbcTemplate.query(sqlStatus, new Object[]{userId, friendId}, (rs, rowNum) -> rs.getInt("status_id"));

        if (statusList.isEmpty()) {
            sqlAddFriendRequest = "INSERT INTO user_friends (user_id, friend_id, status_id) VALUES (?,?,2)";
            jdbcTemplate.update(sqlAddFriendRequest, userId, friendId);
        } else if (statusList.get(0) == 1) {
            throw new RuntimeException("Запрос на добавление пользователя с id " + friendId + " уже отправлен");
        }
    }

    /*
        Часть кода для работы со статусами, которые в Тестах и ТЗ работают в упрощенном виде
            if (status == null) {
                sqlAddFriendRequest = "INSERT INTO user_friends (user_id, friend_id, status_id) VALUES (?,?,1)";
                sqlIncomingRequest = "INSERT INTO user_friends (user_id, friend_id, status_id) VALUES (?,?,3)";
                jdbcTemplate.update(sqlAddFriendRequest, userId, friendId);
                jdbcTemplate.update(sqlIncomingRequest, friendId, userId);
            } else if (status == 3) {

     */
    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlDelFriendRequest = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlDelFriendRequest, userId, friendId);
    }

    @Override
    public List<Integer> getUserFriendsIds(int userId) {
        String sqlGetFriendsIds = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        List<Integer> userFriendsIds = jdbcTemplate.query(sqlGetFriendsIds, new Object[]{userId}, (rs, rowNum) -> rs.getInt("friend_id"));
        if (userFriendsIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userFriendsIds;
    }
}
