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
        isValidId(id);
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
        isValidId(id);
        users.remove(id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public User getUser(long id) {
        log.info("Получение пользователя с id {}", id);
        isValidId(id);
        User user = users.get(id);
        log.info("Пользователь с id {} предоставлен: {}", id, user);
        return user;
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("Получение списка друзей пользователя с id {}", id);
        User user = getUser(id);
        List<Long> friendsId = new ArrayList<>(user.getFriends());
        List<User> friends = new ArrayList<>();
        for (Long friendId : friendsId) {
            User friend = users.get(friendId);
            friends.add(friend);
        }
        log.info("Список друзей пользователя с id {} предоставлен", id);
        return friends;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под id {} в параметре name присвоено значение login", user.getId());
        }
    }

    private void isValidId(long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
    }
}
