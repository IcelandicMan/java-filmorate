package ru.yandex.practicum.filmorate.model;
/*
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void testEmptyEmail() {
        User user = new User();
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Электронная почта не может быть пустой", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void testInvalidEmailFormat() {
        User user = new User();
        user.setEmail("invalid_email");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Некорректный формат электронной почты", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void testEmptyUsername() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может быть пустым", violation.getMessage());
        assertEquals("username", violation.getPropertyPath().toString());
    }

    @Test
    public void testUsernameWithWhitespace() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user name");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может содержать пробелы", violation.getMessage());
        assertEquals("username", violation.getPropertyPath().toString());
    }

    @Test
    public void testEmptyDisplayName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());

        assertEquals("testuser", user.getName());
    }

    @Test
    public void testValidBirthDate() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void testFutureBirthDate() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Дата рождения не может быть в будущем", violation.getMessage());
        assertEquals("birthDate", violation.getPropertyPath().toString());
    }

}
*/