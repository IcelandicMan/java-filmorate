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

    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return userService.getUserStorage().getUsers();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {} ", user);
        userService.getUserStorage().createUser(user);
        log.info("Пользователь добавлен: {} ", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя: {} ", updatedUser);
        userService.getUserStorage().updateUser(updatedUser);
        return updatedUser;
    }
}


