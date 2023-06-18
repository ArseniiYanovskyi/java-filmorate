package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.repositories.InMemoryFilmRepository;
import ru.yandex.practicum.filmorate.repositories.InMemoryUserRepository;
import ru.yandex.practicum.filmorate.repositories.UserRepository;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.service.ValidatorImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserControllerTests {
    private UserController userController;

    @BeforeEach
    void resetController() {
        FilmRepository filmRepository = new InMemoryFilmRepository();
        UserRepository userRepository = new InMemoryUserRepository();

        userController = new UserController(new UserServiceImpl(userRepository),
                new ValidatorImpl(filmRepository, userRepository));
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

        userForEditing.setId(789);
        Assertions.assertThrows(NotFoundException.class, () -> userController.putUser(userForEditing));
    }

    @Test
    void shouldReturnErrorsForBadUserValidation() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex@yandex.ru");
        user.setLogin("    ");
        user.setBirthday(LocalDate.of(1990, 5, 20));

        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());

        user.setLogin("AlexLogin");
        user.setEmail("Alex_yandex.ru");

        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());

        user.setEmail("Alex@yandex.ru");
        user.setBirthday(LocalDate.of(2033, 7, 25));

        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));

        Assertions.assertEquals(new ArrayList<>(), userController.getUsersList());
    }
}