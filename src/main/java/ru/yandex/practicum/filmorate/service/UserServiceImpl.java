package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UsersDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersDao userRepository;
    private final Logger log = LoggerFactory.getLogger("UserService");

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User addUser(User user) {
        checkUserValidation(user);

        log.debug("User Name: {} Email: {} adding in progress.",
                user.getName(), user.getEmail());
        return userRepository.addUser(user);
    }

    public User getRequiredUserById(int id) {
        return userRepository.getOptionalOfUserById(id)
                .orElseThrow(() -> new NotFoundException("User with this Id does not exist in repository."));
    }

    @Override
    public User updateUser(User user) {
        checkUserValidation(user);

        checkIdUserForPresentsInRepository(user.getId());

        log.debug("User ID {} Email: {} updating in progress.", user.getId(), user.getEmail());
        return userRepository.updateUser(user);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User firstUser = userRepository.getOptionalOfUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with Id" + userId + " does not exist in repository."));
        User secondUser = userRepository.getOptionalOfUserById(friendId)
                .orElseThrow(() -> new NotFoundException("User with Id" + friendId + " does not exist in repository."));

        log.debug("Checking possibility to send friendship from ID {} to ID {}", userId, friendId);
        if (getFriendsList(firstUser.getId()).contains(secondUser)) {
            throw new ValidationException("Validation has failed, user already added as friend.");
        }

        userRepository.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User firstUser = userRepository.getOptionalOfUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with Id" + userId + " does not exist in repository."));
        User secondUser = userRepository.getOptionalOfUserById(friendId)
                .orElseThrow(() -> new NotFoundException("User with Id" + friendId + " does not exist in repository."));

        log.debug("Checking possibility to delete from friend list of user ID {} user with ID {}", userId, friendId);
        if (getFriendsList(firstUser.getId()).isEmpty()) {
            throw new ValidationException("Validation has failed, user friends list empty.");
        }
        if (!getFriendsList(firstUser.getId()).contains(secondUser)) {
            throw new ValidationException("Validation has failed, user has not added as friend.");
        }

        userRepository.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriendsList(int id) {
        checkIdUserForPresentsInRepository(id);

        return userRepository.getFriendsList(id);
    }

    @Override
    public List<User> getMutualFriendsList(int firstUserId, int secondUserId) {
        checkIdUserForPresentsInRepository(firstUserId);
        checkIdUserForPresentsInRepository(secondUserId);

        return userRepository.getMutualFriends(firstUserId, secondUserId);
    }

    @Override
    public void checkUserValidation(User user) {
        if (user.getEmail() == null || !isEmailValid(user.getEmail())) {
            log.debug("Validation for user has failed. Incorrect email.");
            throw new ValidationException("Validation for adding user has failed. Incorrect email.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Validation for user has failed. Incorrect login(might be spaces).");
            throw new ValidationException("Validation for adding user has failed. Incorrect login(might be spaces).");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Validation for user has failed. Incorrect birthdate, might be: birthdate is future.");
            throw new ValidationException("Validation for adding user has failed. Incorrect birthdate, might be: birthdate is future.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Validation for new user was successfully finished. But named replaces with login.");
            return;
        }
        log.debug("Validation for new user was successfully finished.");
    }

    @Override
    public void checkIdUserForPresentsInRepository(int id) {
        User user = userRepository.getOptionalOfUserById(id)
                .orElseThrow(() -> new NotFoundException("User with Id: " + id + " does not exist in repository."));
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
}
