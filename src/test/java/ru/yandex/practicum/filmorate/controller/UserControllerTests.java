package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

public class UserControllerTests {

    private UserController userController;

    @BeforeEach
    public void setup() {
        userController = new UserController();
    }

    @Test
    public void createUser_WithEmptyEmail_ShouldThrowValidationException() {
        User user = new User();
        user.setLogin("test");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUser_WithValidData_ShouldReturnCreatedUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.createUser(user);

        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals("test@example.com", createdUser.getEmail());
        Assertions.assertEquals("test", createdUser.getLogin());
        Assertions.assertEquals("John Doe", createdUser.getName());
        Assertions.assertEquals(LocalDate.of(1990, 1, 1), createdUser.getBirthday());
    }
}