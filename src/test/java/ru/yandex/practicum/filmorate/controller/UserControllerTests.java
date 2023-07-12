package ru.yandex.practicum.filmorate.controller;
/*
        import org.junit.jupiter.api.*;

        import static org.junit.jupiter.api.Assertions.*;

        import ru.yandex.practicum.filmorate.model.User;
        import ru.yandex.practicum.filmorate.service.user.UserService;
        import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

        import java.time.LocalDate;

public class UserControllerTests {

    private UserController userController;

    @BeforeEach
    public void setup() {
        userController = new UserController(new UserService(new BD()));
    }


    public void createFriends() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("Arthur");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userController.createUser(user);
        long userId = createdUser.getId();

        assertEquals(createdUser, userController.getUser(userId));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("user@example.com");
        updatedUser.setLogin("updatedLogin");
        updatedUser.setName("updatedUser");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        User upUser = userController.updateUser(updatedUser);

        assertEquals(upUser, userController.getUser(userId));

        User friend = new User();
        friend.setEmail("friend@example.com");
        friend.setLogin("friendLogin");
        friend.setName("Friend");
        friend.setBirthday(LocalDate.of(1990, 1, 1));

        User createdFriend = userController.createUser(friend);
        long friendId = createdFriend.getId();

        assertEquals(2, userController.getUsers().size());
        assertEquals(upUser, userController.getUsers().get(0));
        assertEquals(friend, userController.getUsers().get(1));

        userController.addFriend(userId, friendId);

        assertEquals(1, userController.getUserFriends(userId).size());
        assertEquals(createdFriend, userController.getUserFriends(userId).get(0));

        userController.deleteFriend(userId, friendId);
        assertEquals(0, userController.getUserFriends(userId).size());
    }


}
 */