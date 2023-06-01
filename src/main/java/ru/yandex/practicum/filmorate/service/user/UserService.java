package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        log.info("Запрос от пользователя с id {}, на в друзья пользователя с id {}", userId, friendId);
        userStorage.isValidUserId(userId);
        userStorage.isValidUserId(friendId);

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().add(friendId);
        log.info("Пользователь с id {}, в друзья пользователя с id {}", userId, friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {}, автоматически добавил в друзья пользователя с id {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Запрос от пользователя с id {}, на удаление из друзей пользователя с id {}", userId, friendId);
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
        log.info("Запрос от пользователя с id {}, на получения списка всех общих друзей с пользователем с id {}",
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
        log.info("Списко общих друзей пользователей под id {} и {} создан", userId, friendId);
        return commonFriends;
    }

    public InMemoryUserStorage getUserStorage() {
        return userStorage;
    }
}
