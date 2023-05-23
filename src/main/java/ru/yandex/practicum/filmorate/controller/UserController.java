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
    private Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {} ", user);
        isValid(user);
        user.setId(++idCounter);
        assignNameIfEmpty(user);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {} ", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя: {} ", updatedUser);
        isValid(updatedUser);
        int id = updatedUser.getId();
        if (users.containsKey(id)) {
            User user = users.get(id);
            if (updatedUser.getLogin() != null) {
                user.setLogin(updatedUser.getLogin());
            }
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
                assignNameIfEmpty(updatedUser);
            }
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getBirthday() != null) {
                user.setBirthday(updatedUser.getBirthday());
            }
            log.info("Пользователь под ID {} обновлен: {} ", id, user);
            return user;
        }
        log.warn("Пользователь под ID {} не найден", id);
        throw new ValidationException("Пользователь с данным ID не найден");
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю под ID {} в параметре name присвоено значение login", user.getId());
        }
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


