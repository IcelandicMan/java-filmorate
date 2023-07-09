package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        String sql = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();
        if (!generatedKeys.isEmpty()) {
            Map<String, Object> keysMap = generatedKeys.get(0); // Предполагаем, что нужен первый сгенерированный ключ
            int generatedId = (Integer) keysMap.get("user_id");
            User createdUser = new User(generatedId, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            log.info("Пользователь под id {} создан: {}", createdUser.getId(), createdUser);
            return createdUser;
        } else {
            throw new RuntimeException("Failed to retrieve generated keys");
        }
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя под id {}: {}", updatedUser.getId(), updatedUser);
        String sql = "Update users SET " +
                "user_email = ?, " +
                "user_login = ?, " +
                "user_name = ?, u" +
                "ser_birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sql, updatedUser.getEmail(), updatedUser.getLogin(), updatedUser.getName(),
                java.sql.Date.valueOf(updatedUser.getBirthday()), updatedUser.getId());
        log.info("Пользователь под id {} обновлен: {} ", updatedUser.getId(), updatedUser);
        return getUser(updatedUser.getId());
    }

    @Override
    public void deleteUser(int id) {
        log.info("Удаление пользователя с id {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public User getUser(int id) {
        log.info("Получение пользователя с id {}", id);
        User user = null;
        String sqlUserId = "SELECT user_id FROM users WHERE user_id = ?";
        List<Integer> userIdList = jdbcTemplate.query(sqlUserId, new Object[]{id}, (rs, rowNum) -> rs.getInt("user_id"));

        if (userIdList.isEmpty()) {
            log.error("Пользовател под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        } else if (userIdList.get(0) == id) {
            String sql = "SELECT * FROM users WHERE user_id  = ?";
            user = jdbcTemplate.queryForObject(sql, userRowMapper(), id);
            log.info("Пользователь с id {} получен: {}", id, user);
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        String sql = "SELECT * FROM users ORDER by user_id";
        List<User> usersList = jdbcTemplate.query(sql, userRowMapper());
        log.info("Список всех пользователей получен");
        return usersList;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("user_email"));
            user.setLogin(rs.getString("user_login"));
            user.setName(rs.getString("user_name"));
            user.setBirthday(rs.getDate("user_birthday").toLocalDate());
            return user;
        };
    }
}
