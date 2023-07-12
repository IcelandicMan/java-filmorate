package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        user.setId(++idCounter);
        users.put(user.getId(), user);
        log.info("Пользователь под id {} создан: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя под id {}: {}", updatedUser.getId(), updatedUser);
        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователь под id {} обновлен: {} ", updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(int id) {
        log.info("Удаление пользователя с id {}", id);
        users.remove(id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public User getUser(int id) {
        log.info("Получение пользователя с id {}", id);
        User user = users.get(id);
        if (user == null) {
            log.error("Пользовател под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
        log.info("Пользователь с id {} получен: {}", id, user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Список всех пользователей получен");
        return usersList;
    }
}
