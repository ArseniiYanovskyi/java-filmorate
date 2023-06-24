package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getOptionalOfUserById(int id);

    List<User> getFriendsList(int id);

    void addFriendsAsMutual(int oneUserId, int anotherUserId);

    List<User> getMutualFriends(int firstUserId, int secondUserId);

    void deleteFriendsAsMutual(int oneUserId, int anotherUserId);

    void clear();
}
