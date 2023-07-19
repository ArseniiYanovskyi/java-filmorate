package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final Logger log;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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

        return userService.addUser(user);
    }


    @PutMapping(value = "/users", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.OK)
    public User putUser(@Valid @RequestBody User user) {
        log.debug("Received request to edit existing user.");

        return userService.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void addFriend(@PathVariable String id, @PathVariable String friendId) {
        log.debug("Received request to from user with ID {} to add user with ID {} as friend.", id, friendId);

        int userIdentifier = Integer.parseInt(id);
        int friendIdentifier = Integer.parseInt(friendId);

        userService.addFriend(userIdentifier, friendIdentifier);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        log.debug("Received request from user with ID {} to delete from friend list ID {}.", id, friendId);

        int userIdentifier = Integer.parseInt(id);
        int friendIdentifier = Integer.parseInt(friendId);

        userService.deleteFriend(userIdentifier, friendIdentifier);
    }

    @GetMapping(value = "/users/{id}/friends")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getFriendsList(@PathVariable String id) {
        log.debug("Received request to get user with ID {} friends list", id);

        int userId = Integer.parseInt(id);

        return userService.getFriendsList(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> getMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        log.debug("Received request to get mutual friends users with IDs {} and {}", id, otherId);

        int userId = Integer.parseInt(id);
        int anotherUserId = Integer.parseInt(otherId);

        return userService.getMutualFriendsList(userId, anotherUserId);
    }

    @GetMapping(value = "/users/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public User getUserById(@PathVariable String id) {
        log.debug("Received request to get user with ID {}", id);

        int userId = Integer.parseInt(id);

        return userService.getRequiredUserById(userId);
    }
}
