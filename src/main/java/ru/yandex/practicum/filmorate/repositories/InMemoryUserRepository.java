package ru.yandex.practicum.filmorate.repositories;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
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
    public void addFriendsAsMutual(int oneUserId, int anotherUserId) {
        usersData.get(oneUserId).addFriend(anotherUserId);
        usersData.get(anotherUserId).addFriend(oneUserId);
    }

    @Override
    public void deleteFriendsAsMutual(int oneUserId, int anotherUserId) {
        usersData.get(oneUserId).removeFriend(anotherUserId);
        usersData.get(anotherUserId).removeFriend(oneUserId);
    }

    @Override
    public List<User> getFriendsList(int id) {
        return usersData.get(id).getFriends().stream()
                .map(friendId -> {
                    return usersData.get(Integer.valueOf(friendId));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int firstUserId, int secondUserId) {
        if (usersData.get(firstUserId).getFriends() == null || usersData.get(secondUserId).getFriends() == null) {
            return new ArrayList<User>();
        }
        return usersData.get(firstUserId).getFriends().stream()
                .filter(friendId -> usersData.get(secondUserId).getFriends().contains(friendId))
                .map(friendId -> {
                    return usersData.get(friendId);
                })
                .collect(Collectors.toList());
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
