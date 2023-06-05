package ru.yandex.practicum.filmorate.repositories;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exceptions.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryUserRepository {
    private HashMap<Integer, User> usersData = new HashMap<>();

    public void addUser(User user) {
        if (!isUserPresent(user)) {
            usersData.put(user.getId(), user);
        } else {
            throw new ValidationException("Failed to add new user, id field should be empty.");
        }
    }

    public void editAlreadyExistingUser(User user) {
        if (isUserPresent(user)) {
            usersData.put(user.getId(), user);
        } else {
            throw new IncorrectRequestException(HttpStatus.NOT_FOUND, "This user has not been found in database.");
        }
    }

    public boolean isUserPresent(User user) {
        return usersData.containsKey(user.getId());
    }

    public User getUserById(int id) {
        return usersData.get(id);
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(usersData.values());
    }
}
