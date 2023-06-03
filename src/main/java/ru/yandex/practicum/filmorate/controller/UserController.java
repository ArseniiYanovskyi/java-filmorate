package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.InMemoryUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UserController {
    private static final InMemoryUserRepository usersRepository = new InMemoryUserRepository();
    private static final Logger log = LoggerFactory.getLogger("UserController");

    @GetMapping("/users")
    public List<User> getUsersList() {
        log.debug("Received request for users list.");
        return usersRepository.getAllUsers();
    }

    @PostMapping("/users")
    @ExceptionHandler
    public User postUser(@RequestBody User user) {
        if(!isUserValid(user)) {
            throw new ValidationException("Validation for adding user has failed.");
        }
        if(usersRepository.isUserPresent(user)) {
            log.debug("Failed to add new user, user with this Email already exist.");
            throw new ValidationException("Failed to add new user.");
        }
        usersRepository.addUser(user);
        log.debug("User ID {} Name {} Email {} added successfully.",
                user.getId(), user.getName(), user.getEmail());
        return usersRepository.getUserById(user.getId());
    }


    @PutMapping("/users")
    @ExceptionHandler
    public User putUser(@RequestBody User user) {
        if(!isUserValid(user)) {
            throw new ValidationException("Validation for updating user has failed.");
        }

        if (!usersRepository.isUserPresent(user)) {
            log.debug("Editing user has failed, user has not been found.");
            throw new IncorrectRequestException(HttpStatus.NOT_FOUND, "User with this email does not present in database.");
        }

        usersRepository.editAlreadyExistingUser(user);
        log.debug("User ID {} Name {} Email {} added or edited successfully.",
                user.getId(), user.getName(), user.getEmail());
        return usersRepository.getUserById(user.getId());
    }

    private boolean isUserValid(User user) {
        if (user.getId() == 0) {
            log.debug("Setting new id for user.");
            user.setId(usersRepository.getAllUsers().size() + 1);
        }
        if (user.getEmail() == null || !isEmailValid(user.getEmail())) {
            log.debug("Validation for user has failed. Incorrect email.");
            return false;
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Validation for user has failed. Incorrect login(might be spaces).");
            return false;
        }
        if (user.getBirthday() == null || !isBirthDateValid(user.getBirthday())) {
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

    private boolean isBirthDateValid(String birthDateLine) {
        String[] splitBirthDateLine = birthDateLine.split("-");

        int birthYear = Integer.parseInt(splitBirthDateLine[0]);
        int birthMonth = Integer.parseInt(splitBirthDateLine[1]);
        int birthDay = Integer.parseInt(splitBirthDateLine[2]);

        LocalDateTime birthDate = LocalDateTime.of(birthYear, birthMonth, birthDay, 0, 0, 0);
        LocalDateTime now = LocalDateTime.now();

        if(birthDate.isAfter(now)){
            return false;
        }
        return true;
    }


    private boolean isEmailValid(String email){
        if (!email.contains("@")){
            return false;
        }
        return true;
    }
}
