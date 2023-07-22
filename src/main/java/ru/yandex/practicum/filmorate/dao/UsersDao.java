package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersDao {
    List<User> getAll();

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getOptionalOfUserById(int id);

    List<User> getFriendsList(int id);

    void addFriend(int oneUserId, int anotherUserId);

    List<User> getMutualFriends(int firstUserId, int secondUserId);

    void deleteFriend(int oneUserId, int anotherUserId);
}
