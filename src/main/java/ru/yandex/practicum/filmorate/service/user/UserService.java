package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
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

    public User createUser(User user) {
        assignNameIfEmpty(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        userStorage.getUser(updatedUser.getId());
        assignNameIfEmpty(updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    public void deleteUser(long id) {
        userStorage.getUser(id);
        userStorage.deleteUser(id);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        List<User> usersList = userStorage.getUsers();
        log.info("Список всех пользователей получен");
        return usersList;
    }

    public void addFriend(long userId, long friendId) {
        log.info("Добавление пользователя с id {} в друзья пользователя с id {}", userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().add(friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {} автоматически добавил в друзья пользователя с id {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", userId, friendId);
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

        final User user = userStorage.getUser(userId);
        final User friend = userStorage.getUser(friendId);

        final Set<Long> userFriends = user.getFriends();
        final Set<Long> friendFriends = friend.getFriends();

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
        User user = userStorage.getUser(userId);
        List<Long> friendsIds = new ArrayList<>(user.getFriends());
        List<User> userFriends = new ArrayList<>();
        for (Long friendId : friendsIds) {
            User friend = userStorage.getUser(friendId);
            userFriends.add(friend);
        }
        log.info("Список всех друзей пользователя с id {} получен", userId);
        return userFriends;
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под id {} в параметре name присвоено значение login", user.getId());
        }
    }
}
