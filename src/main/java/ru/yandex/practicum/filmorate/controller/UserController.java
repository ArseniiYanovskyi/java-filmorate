package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.InMemoryUserRepository;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
public class UserController {
    private static final UserService userService = new UserServiceImpl(new InMemoryUserRepository());
    private static final Logger log = LoggerFactory.getLogger("UserController");

    @GetMapping("/users")
    public List<User> getUsersList() {
        log.debug("Received request for users list.");
        return userService.getAll();
    }

    @PostMapping("/users")
    @ExceptionHandler
    public User postUser(@Valid @RequestBody User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Validation for adding user has failed.");
        }

        log.debug("User ID {} Name: {} Email: {} adding in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.addUser(user);
    }


    @PutMapping("/users")
    @ExceptionHandler
    public User putUser(@Valid @RequestBody User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Validation for updating user has failed.");
        }

        log.debug("User ID {} Name: {} Email: {} editing in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.updateUser(user);
    }

    private boolean isUserValid(User user) {
        if (user.getId() == 0) {
            log.debug("Setting new id for user.");
            user.setId(userService.getAll().size() + 1);
        }
        if (user.getEmail() == null || !isEmailValid(user.getEmail())) {
            log.debug("Validation for user has failed. Incorrect email.");
            return false;
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Validation for user has failed. Incorrect login(might be spaces).");
            return false;
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Validation for user has failed. Incorrect birthdate, might be: birthdate is future.");
            return false;
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Validation for new user was successfully finished. But named replaces with login.");
            return true;
        }
        log.debug("Validation for new user was successfully finished.");
        return true;
    }


    private boolean isEmailValid(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return true;
    }

    public void deleteAllUsers() {
        userService.clearRepository();
    }
}
