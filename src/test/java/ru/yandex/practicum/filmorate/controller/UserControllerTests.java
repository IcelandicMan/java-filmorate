package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTests {

    private UserController userController;


    @Test
    public void createEmptyUser_ShouldThrowValidationException() {
        User user = new User();

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUser_WithEmptyEmail_ShouldThrowValidationException() {
        User user = new User();
        user.setLogin("test");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUser_WithEmptyLogin_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void createUser_WithFutureDate_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("test");
        user.setName("Arthur");
        user.setBirthday(LocalDate.now().plusYears(10));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }


    @Test
    public void createUser_WithValidData_ShouldReturnCreatedUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("test", createdUser.getLogin());
        assertEquals("Arthur", createdUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), createdUser.getBirthday());
    }

    @Test
    public void createUser_WithoutName_ShouldReturnCreatedUse_WithLoginInName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("test", createdUser.getLogin());
        assertEquals("test", createdUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), createdUser.getBirthday());
    }


}