package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryUserRepository {
    private HashMap <Integer, User> usersData = new HashMap<>();

    public void addUser(User user){
        usersData.put(user.getId(), user);
    }

    public void editAlreadyExistingUser(User user){
        usersData.put(user.getId(), user);
    }

    public boolean isUserPresent(User user){
        return usersData.containsKey(user.getId());
    }

    public User getUserById(int id){
        return usersData.get(id);
    }

    public ArrayList<User> getAllUsers(){
        return new ArrayList<>(usersData.values());
    }
}
