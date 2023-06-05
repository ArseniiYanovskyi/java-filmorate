package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public List<User> getAll();

    public User addUser(User user);

    public Optional<User> getOptionalOfRequiredUserById(int id);

    public User updateUser(User user);

    public void clearRepository();
}
