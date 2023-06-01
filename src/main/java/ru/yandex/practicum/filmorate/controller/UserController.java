package ru.yandex.practicum.filmorate.controller;

import javax.validation.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.info("Запрошен пользователь с id: {} ", id);
        User user = userService.getUserStorage().getUser(id);
        log.info("Запрос на пользователя с id {} выполнен: {} ", id, user);
        return user;
    }

    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список Всех пользователей");
        return userService.getUserStorage().getUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        log.info("Запрошен список всех друзей пользователя под id {} ", id);
        List<User> friends = userService.getUserStorage().getUserFriends(id);
        log.info("Запрос на предоставление списка друзкй пользователя с id {} обработан", id);
        return friends;

    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("От Пользователя с id {} запрошен список общих друзей с пользователем с id {} ", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Список общих друзей пользователя с id {} с пользователем с id {} предоставлен ", id, otherId);
        return commonFriends;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрошено создание пользователя: {} ", user);
        userService.getUserStorage().createUser(user);
        log.info("Запрос обработан, пользователь создан: {} ", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Запрошено обновление пользователя: {} ", updatedUser);
        userService.getUserStorage().updateUser(updatedUser);
        log.info("Запрос обработан, пользователь обновлен: {} ", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("От Пользователя с id {} запрошено добавление в друзья пользователя с id {} ", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь с id {} и пользователь с id {} друзья", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("От Пользователя с id {} запрошено удаление из друзей пользователя с id {} ", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователь id {} удален из друзей пользвателя с id {}", friendId, id);
    }

    //Не безопасный запрос
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление Пользователся с id {} ", id);
        userService.getUserStorage().deleteUser(id);
        log.info("Пользователь id {} удален", id);
    }
}


