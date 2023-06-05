package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void create


    public void addFriend(long userId, long friendId) {
        log.info("Добавление пользователя с id {} в друзья пользователя с id {}", userId, friendId);
        if (userStorage.getUser(userId) == null || userStorage.getUser(friendId) == null ){

        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().add(friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {} автоматически добавил в друзья пользователя с id {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", userId, friendId);
        userStorage.isValidUserId(userId);
        userStorage.isValidUserId(friendId);

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().remove(friendId);
        log.info("Пользователь с id {}, удалил из друзей пользователя с id {}", userId, friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id {}, автоматически удалил из друзей пользователя с id {}", friendId, userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        log.info("Получение списка всех общих друзей пользователся с id {} с пользователем с id {}",
                userId, friendId);

        userStorage.isValidUserId(userId);
        userStorage.isValidUserId(friendId);

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();

        List<User> commonFriends = new ArrayList<>();

        for (Long commonFriendId : userFriends) {
            if (friendFriends.contains(commonFriendId)) {
                User commonFriend = userStorage.getUser(commonFriendId);
                commonFriends.add(commonFriend);
            }
        }
        log.info("Список общих друзей пользователей под id {} и {} предоставлен", userId, friendId);
        return commonFriends;
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

    public UserStorage getUserStorage() {
        return userStorage;
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под id {} в параметре name присвоено значение login", user.getId());
        }
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

}
