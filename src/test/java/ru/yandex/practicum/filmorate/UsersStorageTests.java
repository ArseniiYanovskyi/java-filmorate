package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UsersDBStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@Import(UsersDBStorage.class)
public class UsersStorageTests {
    @Autowired
    private UsersDBStorage usersStorage;

    @Test
    void shouldAddUserCorrectly() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("AlexLogin");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        usersStorage.addUser(user);

        List<User> usersList = new ArrayList<>();
        usersList.add(user);

        Assertions.assertEquals(usersList, usersStorage.getAll());
        Assertions.assertEquals(user, usersStorage.getAll().get(0));
        Assertions.assertEquals(user.getName(), usersStorage.getAll().get(0).getName());
        Assertions.assertEquals(user.getEmail(), usersStorage.getAll().get(0).getEmail());
        Assertions.assertEquals(user.getLogin(), usersStorage.getAll().get(0).getLogin());
        Assertions.assertEquals(user.getBirthday(), usersStorage.getAll().get(0).getBirthday());

        User anotherUser = new User();
        anotherUser.setName("John");
        anotherUser.setEmail("John@yandex.ru");
        anotherUser.setLogin("JohnLogin");
        anotherUser.setBirthday(LocalDate.of(1995, 10, 25));

        usersStorage.addUser(anotherUser);

        usersList.add(anotherUser);

        Assertions.assertEquals(usersList, usersStorage.getAll());
        Assertions.assertEquals(anotherUser, usersStorage.getAll().get(1));
        Assertions.assertEquals(anotherUser.getName(), usersStorage.getAll().get(1).getName());
        Assertions.assertEquals(anotherUser.getEmail(), usersStorage.getAll().get(1).getEmail());
        Assertions.assertEquals(anotherUser.getLogin(), usersStorage.getAll().get(1).getLogin());
        Assertions.assertEquals(anotherUser.getBirthday(), usersStorage.getAll().get(1).getBirthday());
    }

    @Test
    void shouldEditUserCorrectly() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("AlexLogin");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        usersStorage.addUser(user);

        User userForEditing = new User();
        userForEditing.setName("AlexFindley");
        userForEditing.setEmail("AlexFindley@yandex.ru");
        userForEditing.setLogin("AlexFindleyLogin");
        userForEditing.setBirthday(LocalDate.of(1995, 10, 26));
        userForEditing.setId(usersStorage.getAll().get(0).getId());

        usersStorage.updateUser(userForEditing);

        Assertions.assertEquals(userForEditing, usersStorage.getAll().get(0));
        Assertions.assertEquals(userForEditing.getId(), usersStorage.getAll().get(0).getId());
        Assertions.assertEquals(userForEditing.getName(), usersStorage.getAll().get(0).getName());
        Assertions.assertEquals(userForEditing.getEmail(), usersStorage.getAll().get(0).getEmail());
        Assertions.assertEquals(userForEditing.getLogin(), usersStorage.getAll().get(0).getLogin());
        Assertions.assertEquals(userForEditing.getBirthday(), usersStorage.getAll().get(0).getBirthday());
    }

    @Test
    void shoutReturnCorrectlyById() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("AlexLogin");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        usersStorage.addUser(user);

        Assertions.assertEquals(user.getName(), usersStorage.getOptionalOfUserById(1).get().getName());
        Assertions.assertEquals(user.getEmail(), usersStorage.getOptionalOfUserById(1).get().getEmail());
        Assertions.assertEquals(user.getLogin(), usersStorage.getOptionalOfUserById(1).get().getLogin());
        Assertions.assertEquals(user.getBirthday(), usersStorage.getOptionalOfUserById(1).get().getBirthday());
    }

    @Test
    void shouldAddAndDeleteFriendsCorrectly() {
        List<User> correctFriendsList = new ArrayList<>();

        User user = new User();
        user.setName("user");
        user.setEmail("user@yandex.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.of(1995, 10, 20));
        usersStorage.addUser(user);

        Assertions.assertEquals(correctFriendsList, usersStorage.getFriendsList(1));

        for (int i = 1; i < 6; i++) {
            user = new User();
            user.setName("user");
            user.setEmail("user@yandex.ru");
            user.setLogin("user");
            user.setBirthday(LocalDate.of(1995, 10, 20));
            usersStorage.addUser(user);
            user.setId(i + 1);
            correctFriendsList.add(user);
        }

        usersStorage.addFriend(1, 2);
        usersStorage.addFriend(1, 3);
        usersStorage.addFriend(1, 4);
        usersStorage.addFriend(1, 5);
        usersStorage.addFriend(1, 6);


        Assertions.assertEquals(correctFriendsList, usersStorage.getFriendsList(1));

        usersStorage.deleteFriend(1, 5);
        correctFriendsList.remove(3);

        Assertions.assertEquals(correctFriendsList, usersStorage.getFriendsList(1));

        usersStorage.addFriend(2, 6);
        usersStorage.addFriend(2, 1);

        List<User> correctMutualFriendsList = new ArrayList<>();
        correctMutualFriendsList.add(user);
        Assertions.assertEquals(correctMutualFriendsList, usersStorage.getMutualFriends(1, 2));
    }
}
