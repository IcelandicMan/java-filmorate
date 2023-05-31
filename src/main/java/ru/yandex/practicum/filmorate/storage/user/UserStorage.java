package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    User getUser(long id);

    List<User> getUsers();

    List<Long> getFriends(long id);
}
