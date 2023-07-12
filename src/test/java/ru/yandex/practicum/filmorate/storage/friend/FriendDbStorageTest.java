package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {

    private final UserDbStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.createUser(user);

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friendLogin");
        friend.setName("friend");
        friend.setBirthday(LocalDate.of(1992, 1, 1));

        userStorage.createUser(friend);
    }

    @Test
    void addFriendsThenDeleteFriends() {
        friendDbStorage.addFriend(1, 2);
        List<Integer> friends1 = friendDbStorage.getUserFriendsIds(1);

        assertEquals(1, friends1.size());
        assertEquals(2, friends1.get(0));

        List<Integer> friends2 = friendDbStorage.getUserFriendsIds(2);
        assertEquals(0, friends2.size());

        friendDbStorage.addFriend(2, 1);
        friends2 = friendDbStorage.getUserFriendsIds(2);
        assertEquals(1, friends2.size());
        assertEquals(1, friends2.get(0));

        friendDbStorage.deleteFriend(1, 2);
        friends1 = friendDbStorage.getUserFriendsIds(1);
        assertEquals(0, friends1.size());

        friends2 = friendDbStorage.getUserFriendsIds(2);
        assertEquals(1, friends2.size());
        assertEquals(1, friends2.get(0));
    }
}