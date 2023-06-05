package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public List<User> getAll();
    
    public User addUser(User user);

    public User updateUser(User user);

    public void clearRepository();
}
