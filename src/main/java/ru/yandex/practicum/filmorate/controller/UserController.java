package ru.yandex.practicum.filmorate.controller;

import javax.validation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return null;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {} ", user);
        isValid(user);

        log.info("Пользователь добавлен: {} ", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя: {} ", updatedUser);
        isValid(updatedUser);
        return null;
    }


    private void isValid(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
    }
}


