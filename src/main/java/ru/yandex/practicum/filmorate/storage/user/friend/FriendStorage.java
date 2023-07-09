package ru.yandex.practicum.filmorate.storage.user.friend;

import java.util.List;

public interface FriendStorage {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<Integer> getUserFriendsIds(int userId);
}
