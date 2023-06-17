package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();

    User addUser(User user);

    Optional<User> getOptionalOfRequiredUserById(int id);

    User updateUser(User user);

    void addMutualFriend(int userId, int friendId);

    void removeMutualFriends(int userId, int friendId);

    List<User> getMutualFriendsList(int firstUserId, int secondUserId);

    List<User> getFriendsList(int id);

    void clearRepository();
}
