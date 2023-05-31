package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private int idCounter = 0;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        user.setId(++idCounter);
        assignNameIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Пользователь под id {} создан: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        long id = updatedUser.getId();
        log.info("Обновление пользователя под id {}: {}", id, updatedUser);
        if (isValidId(id)) {
            User user = users.get(id);
            if (updatedUser.getLogin() != null) {
                user.setLogin(updatedUser.getLogin());
            }
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
                assignNameIfEmpty(updatedUser);
            }
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getBirthday() != null) {
                user.setBirthday(updatedUser.getBirthday());
            }
            log.info("Пользователь под id {} обновлен: {} ", id, user);
            return user;
        }
        log.warn("Пользователь под id {} не найден", id);
        throw new ValidationException("Пользователь с данным ID не найден");
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<Long> getFriends(long id) {
        User user = getUser(id);
        return new ArrayList<>(user.getFriends());
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под ID {} в параметре name присвоено значение login", user.getId());
        }
    }

    private boolean isValidId(long id) {
    if (users.containsKey(id)){
        return true;
    }
        throw new ValidationException("Пользователь с данным ID не найден");
    }


}
