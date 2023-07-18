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
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
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
            int generatedId = (Integer) keysMap.get("id");
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
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "WHERE id = ?";
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
        final String sql = "SELECT * FROM users WHERE id = ?";
        List<User> user = jdbcTemplate.query(sql, userRowMapper(), id);

        if (user.size() != 1) {
            log.error("Пользовател под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
        return user.get(0);
    }

    @Override
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        String sql = "SELECT * FROM users ORDER by id";
        List<User> usersList = jdbcTemplate.query(sql, userRowMapper());
        log.info("Список всех пользователей получен");
        return usersList;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }
}
