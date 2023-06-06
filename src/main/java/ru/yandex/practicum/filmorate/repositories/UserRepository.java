package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public List<User> getAll();

    public User addUser(User user);

    public User updateUser(User user);

    public Optional<User> getOptionalOfUserById(int id);

    public void clear();
}
