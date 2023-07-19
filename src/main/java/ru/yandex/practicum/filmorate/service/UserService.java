package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User addUser(User user);

    User getRequiredUserById(int id);

    User updateUser(User user);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getMutualFriendsList(int firstUserId, int secondUserId);

    List<User> getFriendsList(int id);

    void checkUserValidation(User user);
    void checkIdUserForPresentsInRepository(int id);
}
