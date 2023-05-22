package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private List<User> users = new ArrayList<>();
    private int nextUserId = 1;

    @GetMapping()
    public List<User> getUsers() {
        log.info("Запрошен список пользователей");
        return users;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        if (hasValidationErrors(user)) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
        user.setId(nextUserId++);
        assignNameIfEmpty(user);
        users.add(user);
        log.info("Пользователь добавлен: {} ", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User updatedUser) {
        if (hasValidationErrors(updatedUser)) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
        int id = updatedUser.getId();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getId() == id) {
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
        }
        log.warn("Пользователь под ID {} не найден", id);
        throw new ValidationException("Пользователь с данным ID не найден");
    }

    private void assignNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private boolean hasValidationErrors(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        return !violations.isEmpty();
    }
}


