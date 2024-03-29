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
    public User getUser(@PathVariable int id) {
        log.info("Запрошен пользователь с id: {} ", id);
        User user = userService.getUser(id);
        log.info("Запрос на пользователя с id {} выполнен: {} ", id, user);
        return user;
    }

    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список Всех пользователей");
        List<User> users = userService.getUsers();
        log.info("Запрос на предоставление списка всех пользователей выплнен");
        return users;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрошено создание пользователя: {} ", user);
        User createdUser = userService.createUser(user);
        log.info("Запрос на создание пользователся выполнен, пользователь создан: {} ", createdUser);
        return createdUser;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрошено обновление пользователя: {} ", user);
        User updatedUser = userService.updateUser(user);
        log.info("Запрос выполнен, пользователь обновлен: {} ", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрошен от пользователя с id {} добавление в друзья пользователя с id {} ", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Запрос от пользователя с id {} на добавление в друзья пользователяс id {} выполнен", id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрошен от пользователя с id {} список общих друзей с пользователем с id {} ", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Запрос списка общих друзей пользователя с id {} с пользователем с id {} выполнен", id, otherId);
        return commonFriends;
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        log.info("Запрошен список всех друзей пользователя под id {} ", id);
        List<User> friends = userService.getUserFriends(id);
        log.info("Запрос на предоставление списка друзей пользователя с id {} выполнен", id);
        return friends;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрошен от пользователя с id {} удаление из друзей пользователя с id {} ", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Запрос от пользователя с id id {} на удаление из друзей пользвателя с id {} выполнен", friendId, id);
    }

    //Не безопасный запрос
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Запрошено на удаление пользователся с id {} ", id);
        userService.deleteUser(id);
        log.info("Запрос на удаление пользователя id {} выполнен", id);
    }
}


