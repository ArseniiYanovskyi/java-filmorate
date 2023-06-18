package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.Validator;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final Logger log;
    private final Validator validator;

    @Autowired
    public UserController(UserService userService, Validator validator) {
        this.userService = userService;
        this.validator = validator;
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
        log.debug("Received request to add new user.");

        validator.checkUserValidation(user);

        log.debug("User ID {} Name: {} Email: {} adding in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.addUser(user);
    }


    @PutMapping(value = "/users", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.OK)
    public User putUser(@Valid @RequestBody User user) {
        log.debug("Received request to edit existing user.");

        validator.checkUserValidation(user);
        validator.checkIsUserPresent(user.getId());

        log.debug("User ID {} Name: {} Email: {} editing in progress.",
                user.getId(), user.getName(), user.getEmail());
        return userService.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void makeUsersMutualFriends(@PathVariable String id, @PathVariable String friendId) {
        log.debug("Received request to connect users as friend with ID {} and {}.", id, friendId);

        int oneUserId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(friendId);

        validator.checkIsPossibleToBecomeFriends(oneUserId, anotherUserId);

        userService.addMutualFriend(oneUserId, anotherUserId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void breakMutualFriendship(@PathVariable String id, @PathVariable String friendId) {
        log.debug("Received request to break users friendship with ID {} and {}.", id, friendId);

        int oneUserId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(friendId);

        validator.checkIsPossibleToBreakFriends(oneUserId, anotherUserId);

        userService.removeMutualFriends(oneUserId, anotherUserId);
    }

    @GetMapping(value = "/users/{id}/friends")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getFriendsList(@PathVariable String id) {
        log.debug("Received request to get user with ID {} friends list", id);

        int userId = Integer.parseInt(id);

        validator.checkIsUserPresent(userId);

        return userService.getFriendsList(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        log.debug("Received request to get mutual friends users with IDs {} and {}", id, otherId);

        int userId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(otherId);

        validator.checkIsUserPresent(userId);
        validator.checkIsUserPresent(anotherUserId);

        return userService.getMutualFriendsList(userId, anotherUserId);
    }

    @GetMapping(value = "/users/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public User getUserById(@PathVariable String id) {
        log.debug("Received request to get user with ID {}", id);

        int userId = Integer.parseInt(id);

        if (userService.getOptionalOfRequiredUserById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " has not been found.");
        }

        return userService.getOptionalOfRequiredUserById(userId).get();
    }

    public void deleteAllUsers() {
        userService.clearRepository();
    }
}
