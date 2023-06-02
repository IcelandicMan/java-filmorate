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
        log.info("Обновление пользователя под id {}: {}", updatedUser.getId(), updatedUser);
        long id = updatedUser.getId();
        isValidUserId(id);
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

    @Override
    public void deleteUser(long id) {
        log.info("Удаление пользователя с id {}", id);
        isValidUserId(id);
        users.remove(id);
        log.info("Пользователь с id {} удален", id);
    }


    public User getUser(long id) {
        log.info("Получение пользователя с id {}", id);
        isValidUserId(id);
        User user = users.get(id);
        log.info("Пользователь с id {} получен: {}", id, user);
        return user;
    }

    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Список всех пользователей получен");
        return usersList;
    }

    public List<User> getUserFriends(long userId) {
        log.info("Получение от пользователя с id {} на предоставление списка всех его друзей", userId);
        User user = users.get(userId);
        List<Long> friendsIds = new ArrayList<>(user.getFriends());
        List<User> userFriends = new ArrayList<>();
        for (Long friendId : friendsIds) {
            User friend = users.get(friendId);
            userFriends.add(friend);
        }
        log.info("Список всех друзей пользователя с id {} получен", userId);
        return userFriends;
    }

    public void isValidUserId(long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под id {} в параметре name присвоено значение login", user.getId());
        }
    }
}
