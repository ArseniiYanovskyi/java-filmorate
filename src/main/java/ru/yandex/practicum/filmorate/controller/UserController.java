package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final FilmService filmService;
    private final Logger log;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
        this.log = LoggerFactory.getLogger("UserController");
    }

    @GetMapping("/users")
    public List<User> getUsersList() {
        log.debug("Received request for users list.");
        return userService.getAll();
    }

    @PostMapping(value = "/users", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.CREATED)
    public User postUser(@Valid @RequestBody User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Validation for adding user has failed.");
        }

        log.debug("User ID {} Name: {} Email: {} adding in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.addUser(user);
    }


    @PutMapping(value = "/users", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.OK)
    public User putUser(@Valid @RequestBody User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Validation for updating user has failed.");
        }

        if (user.getId() == 0 || userService.getOptionalOfRequiredUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("User with this Id does not exist in repository.");
        }

        log.debug("User ID {} Name: {} Email: {} editing in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void makeUsersMutualFriends(@PathVariable String id, @PathVariable String friendId) {
        int oneUserId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(friendId);

        log.debug("Received request to connect users as friend with ID {} and {}.", oneUserId, anotherUserId);

        if (userService.getOptionalOfRequiredUserById(oneUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + oneUserId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(anotherUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + anotherUserId + " has not been found.");
        }

        userService.addMutualFriend(oneUserId, anotherUserId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void breakMutualFriendship(@PathVariable String id, @PathVariable String friendId) {
        int oneUserId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(friendId);

        log.debug("Received request to break users friendship with ID {} and {}.", oneUserId, anotherUserId);

        if (userService.getOptionalOfRequiredUserById(oneUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + oneUserId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(anotherUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + anotherUserId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(oneUserId).get().getFriends() == null
                || userService.getOptionalOfRequiredUserById(anotherUserId).get().getFriends() == null) {
            throw new ValidationException("Validation has failed, both or one of users friends list empty.");
        }

        if (!userService.getOptionalOfRequiredUserById(oneUserId).get().getFriends().contains(anotherUserId)
                || !userService.getOptionalOfRequiredUserById(anotherUserId).get().getFriends().contains(oneUserId)) {
            throw new ValidationException("Validation has failed, both or one of users is(are) not connected as friend(s).");
        }

        userService.removeMutualFriends(oneUserId, anotherUserId);
    }

    @GetMapping(value = "/users/{id}/friends")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getFriendsList(@PathVariable String id) {
        int userId = Integer.parseInt(id);

        log.debug("Received request to get user with ID {} friends list", userId);

        if (userService.getOptionalOfRequiredUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " has not been found.");
        }

        return userService.getFriendsList(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        log.debug("Received request to get mutual friends users with IDs {} and {}", id, otherId);
        int userId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(otherId);


        if (userService.getOptionalOfRequiredUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(anotherUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + anotherUserId + " has not been found.");
        }

        return userService.getMutualFriendsList(userId, anotherUserId);
    }

    @GetMapping(value = "/users/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public User getUserById(@PathVariable String id) {
        int userId = Integer.parseInt(id);

        log.debug("Received request to get user with ID {}", userId);

        if (userService.getOptionalOfRequiredUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " has not been found.");
        }

        return userService.getOptionalOfRequiredUserById(userId).get();
    }

    private boolean isUserValid(User user) {
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
        return email.contains("@");
    }

    public void deleteAllUsers() {
        userService.clearRepository();
    }
}
