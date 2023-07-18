package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.createUser(user);
    }

    @Test
    @DisplayName("1. Получаем пользователя " + "\n" +
            "2. Обновляем пользователя и получаем пользователя" + "\n" +
            "3. Создаем друга" + "\n" +
            "4. Получаем список пользователей  " + "\n" +
            "5. Удаляем пользователя  " + "\n" +
            "6. Получаем список пользователей"
    )
    public void getUser() {
        User user = userStorage.getUser(1);

        assertEquals(1, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("userLogin", user.getLogin());
        assertEquals("Arthur", user.getName());

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail("update@example.com");
        updateUser.setLogin("updateLogin");
        updateUser.setName("updateName");
        updateUser.setBirthday(LocalDate.of(1991, 1, 1));

        User updatedUser = userStorage.updateUser(updateUser);

        assertEquals(1, updatedUser.getId());
        assertEquals("update@example.com", updatedUser.getEmail());
        assertEquals("updateLogin", updatedUser.getLogin());
        assertEquals("updateName", updatedUser.getName());

        User getedUser = userStorage.getUser(1);
        assertEquals(1, getedUser.getId());
        assertEquals("update@example.com", getedUser.getEmail());
        assertEquals("updateLogin", getedUser.getLogin());
        assertEquals("updateName", getedUser.getName());

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friendLogin");
        friend.setName("friend");
        friend.setBirthday(LocalDate.of(1992, 1, 1));

        userStorage.createUser(friend);
        User userFriend = userStorage.getUser(2);

        assertEquals(2, userFriend.getId());
        assertEquals("friend@example.com", userFriend.getEmail());
        assertEquals("friendLogin", userFriend.getLogin());
        assertEquals("friend", userFriend.getName());

        List<User> users = userStorage.getUsers();
        assertEquals(2, users.size());
        assertEquals("updateName", users.get(0).getName());
        assertEquals("friend", users.get(1).getName());

        userStorage.deleteUser(1);

        users = userStorage.getUsers();
        assertEquals(1, users.size());
        assertEquals("friend", users.get(0).getName());
    }
}
