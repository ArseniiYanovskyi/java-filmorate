package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {
    private static final HashMap<String, User> usersData = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger("UserController");
    private static int userIdCounter = 1;

    @GetMapping("/users")
    public List<User> getUsersList(){
        log.debug("Received request for users list.");
        return new ArrayList<>(usersData.values());
    }

    @PostMapping("/users")
    @ExceptionHandler
    public User postUser(@RequestBody User user){
        try{
            if(isUserValid(user)) {
                if (user.getId() > userIdCounter){
                    userIdCounter = user.getId() + 1;
                }
                if (user.getId() == 0){
                    user.setId(userIdCounter++);
                }
                usersData.put(user.getEmail(), user);
                log.debug("User ID {} Name {} Email {} added successfully.",
                        user.getId(), user.getName(), user.getEmail());
                return usersData.get(user.getEmail());
            }
        } catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        return user;
    }


    @PutMapping("/users")
    @ExceptionHandler
    public User putUser(@RequestBody User user){
        try{
            if(isUserValid(user)) {
                if (user.getId() > userIdCounter){
                    userIdCounter = user.getId() + 1;
                }
                if (user.getId() == 0){
                    user.setId(userIdCounter++);
                }
                usersData.put(user.getEmail(), user);
                log.debug("User ID {} Name {} Email {} added or edited successfully.",
                        user.getId(), user.getName(), user.getEmail());
                return usersData.get(user.getEmail());
            }
        } catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        return user;
    }

    private boolean isUserValid(User user){
        if (user.getEmail() == null || !isEmailValid(user.getEmail())){
            log.debug("Validation for user has failed. Incorrect email.");
            throw new ValidationException("Incorrect email.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Validation for user has failed. Incorrect login(might be spaces).");
            throw new ValidationException("Incorrect login(might be spaces).");
        }
        if (user.getBirthday() == null || !isBirthDateValid(user.getBirthday())) {
            log.debug("Validation for user has failed. Incorrect birthdate, might be: birthdate is future.");
            throw new ValidationException("Incorrect birthdate, might be: birthdate is future.");
        }
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
            log.debug("Validation for new user was successfully finished. But named replaces with login.");
            return true;
        }
        log.debug("Validation for new user was successfully finished.");
        return true;
    }

    private boolean isBirthDateValid(String birthDateLine){
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
