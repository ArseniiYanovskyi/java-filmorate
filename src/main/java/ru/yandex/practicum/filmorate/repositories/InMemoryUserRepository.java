package ru.yandex.practicum.filmorate.repositories;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private HashMap<Integer, User> usersData = new HashMap<>();
    private int userIdCounter = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersData.values());
    }

    @Override
    public User addUser(User user) {
        if (!isUserPresent(user)) {
            user.setId(++userIdCounter);
            usersData.put(user.getId(), user);
        } else {
            throw new ValidationException("Failed to add new user, id field should be empty.");
        }
        return usersData.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        if (isUserPresent(user)) {
            usersData.put(user.getId(), user);
        } else {
            throw new NotFoundException("This user has not been found in database.");
        }
        return usersData.get(user.getId());
    }

    @Override
    public Optional<User> getOptionalOfUserById(int id) {
        return Optional.ofNullable(usersData.get(id));
    }

    @Override
    public void clear() {
        usersData.clear();
    }

    private boolean isUserPresent(User user) {
        return usersData.containsKey(user.getId());
    }

}
