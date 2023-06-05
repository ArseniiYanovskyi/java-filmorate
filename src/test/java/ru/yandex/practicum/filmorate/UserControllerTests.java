package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserControllerTests {
    private UserController userController;

    @BeforeEach
    void resetController() {
        userController = new UserController();
    }

    @AfterEach
    void clearData() {
        userController.deleteAllUsers();
    }


    @Test
    void shouldAddUserCorrectly() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("AlexLogin");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        userController.postUser(user);

        List<User> usersList = new ArrayList<>();
        usersList.add(user);

        Assertions.assertEquals(usersList, userController.getUsersList());
        Assertions.assertEquals(user, userController.getUsersList().get(0));

        User anotherUser = new User();
        anotherUser.setName("John");
        anotherUser.setEmail("John@yandex.ru");
        anotherUser.setLogin("JohnLogin");
        anotherUser.setBirthday(LocalDate.of(1995, 10, 25));

        userController.postUser(anotherUser);

        usersList.add(anotherUser);

        Assertions.assertEquals(usersList, userController.getUsersList());
        Assertions.assertEquals(anotherUser, userController.getUsersList().get(1));
    }

    @Test
    void shouldEditUserCorrectly() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("AlexLogin");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        userController.postUser(user);

        User userForEditing = new User();
        userForEditing.setName("AlexFindley");
        userForEditing.setEmail("AlexFindley@yandex.ru");
        userForEditing.setLogin("AlexFindleyLogin");
        userForEditing.setBirthday(LocalDate.of(1995, 10, 26));
        userForEditing.setId(userController.getUsersList().get(0).getId());

        userController.putUser(userForEditing);

        Assertions.assertEquals(userForEditing, userController.getUsersList().get(0));
    }

    @Test
    void shouldReturnErrorsForBadUserValidation() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("    ");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        try {
            userController.postUser(user);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            System.out.println("Expected error has occurred for trying to post user with empty login.");
        }

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());

        user.setLogin("AlexLogin");
        user.setEmail("Alex_yandex.ru");

        try {
            userController.postUser(user);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            System.out.println("Expected error has occurred for trying to post user with incorrect email.");
        }

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());

        user.setEmail("Alex@yandex.ru");
        user.setBirthday(LocalDate.of(2033, 7, 25));

        try {
            userController.postUser(user);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            System.out.println("Expected error has occurred for trying to post user with birthday in future.");
        }

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());
    }
}