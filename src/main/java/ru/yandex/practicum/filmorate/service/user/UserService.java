package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @Autowired
    private FriendStorage friendStorage;

    public User createUser(User user) {
        return userStorage.createUser(assignNameIfEmpty(user));
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(assignNameIfEmpty(updatedUser));
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Получение списка всех общих друзей пользователся с id {} с пользователем с id {}",
                userId, friendId);

        final User user = userStorage.getUser(userId);
        final User friend = userStorage.getUser(friendId);

        final List<Integer> userFriends = friendStorage.getUserFriendsIds(user.getId());
        final List<Integer> friendFriends = friendStorage.getUserFriendsIds(friend.getId());

        List<User> commonFriends = new ArrayList<>();

        for (Integer commonFriendId : userFriends) {
            if (friendFriends.contains(commonFriendId)) {
                User commonFriend = userStorage.getUser(commonFriendId);
                commonFriends.add(commonFriend);
            }
        }
        log.info("Список общих друзей пользователей под id {} и {} предоставлен", userId, friendId);
        return commonFriends;
    }

    public List<User> getUserFriends(int userId) {
        log.info("Получение от пользователя с id {} на предоставление списка всех его друзей", userId);
        User user = userStorage.getUser(userId);
        List<Integer> friendsIds = friendStorage.getUserFriendsIds(user.getId());
        List<User> userFriends = new ArrayList<>();
        for (Integer friendId : friendsIds) {
            User friend = userStorage.getUser(friendId);
            userFriends.add(friend);
        }
        log.info("Список всех друзей пользователя с id {} получен", userId);
        return userFriends;
    }

    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", userId, friendId);
        friendStorage.deleteFriend(userId, friendId);
        log.info("Пользователь с id {}, удалил из друзей пользователя с id {}", userId, friendId);
    }

    private User assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под id {} в параметре name присвоено значение login", user.getId());
        }
        return user;
    }
}
