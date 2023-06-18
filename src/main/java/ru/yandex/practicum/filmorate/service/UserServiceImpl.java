package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    public Optional<User> getOptionalOfRequiredUserById(int id) {
        return userRepository.getOptionalOfUserById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public void addMutualFriend(int userId, int friendId) {
        userRepository.addFriendsAsMutual(userId, friendId);
    }

    @Override
    public void removeMutualFriends(int userId, int friendId) {
        userRepository.deleteFriendsAsMutual(userId, friendId);
    }

    @Override
    public List<User> getFriendsList(int id) {
        return userRepository.getFriendsList(id);
    }

    @Override
    public List<User> getMutualFriendsList(int firstUserId, int secondUserId) {
        return userRepository.getMutualFriends(firstUserId, secondUserId);
    }

    @Override
    public void clearRepository() {
        userRepository.clear();
    }
}
