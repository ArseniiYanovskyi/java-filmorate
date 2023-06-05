package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    public List<User> getAll();
    public User addUser(User user);
    public User updateUser(User user);
    public User getUserById(int id);
    public void clear();
}
