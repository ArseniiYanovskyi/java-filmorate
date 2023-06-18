package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.FilmRepository;
import ru.yandex.practicum.filmorate.repositories.UserRepository;

import java.time.LocalDate;

@Service
public class ValidatorImpl implements Validator {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    private final Logger log;

    @Autowired
    public ValidatorImpl(FilmRepository filmRepository, UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.log = LoggerFactory.getLogger("Validator");
    }

    @Override
    public void checkFilmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Validation for film has failed. Incorrect film name, might be: lesser 1 char.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film name, might be: lesser 1 char.");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.debug("Validation for film has failed. Incorrect film description, might be: too long.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film description, might be: too long.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_DATE_OF_BIRTH)) {
            log.debug("Validation for film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
        }
        if (film.getDuration() <= 0) {
            log.debug("Validation for film has failed. Incorrect film duration, might be: lesser than 1 point.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film duration, might be: lesser than 1 point.");
        }
        log.debug("Validation for new film was successfully finished.");
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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    @Override
    public void checkIsFilmPresent(int id) {
        log.debug("Checking film with id {} presents in repository.", id);
        if (filmRepository.getOptionalOfFilmById(id).isEmpty()) {
            throw new NotFoundException("Film with this Id does not exist in repository.");
        }

    }

    @Override
    public void checkIsUserPresent(int id) {
        log.debug("Checking user with id {} presents in repository.", id);
        if (userRepository.getOptionalOfUserById(id).isEmpty()) {
            throw new NotFoundException("User with this Id does not exist in repository.");
        }
    }

    @Override
    public void checkIsPossibleToBecomeFriends(int oneUserId, int anotherUserId) {
        log.debug("Checking possibility to make friendship between {} and {}", oneUserId, anotherUserId);

        if (userRepository.getOptionalOfUserById(oneUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + oneUserId + " has not been found.");
        }

        if (userRepository.getOptionalOfUserById(anotherUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + anotherUserId + " has not been found.");
        }
    }

    @Override
    public void checkIsPossibleToBreakFriends(int oneUserId, int anotherUserId) {
        log.debug("Checking possibility to break friendship between {} and {}", oneUserId, anotherUserId);

        if (userRepository.getOptionalOfUserById(oneUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + oneUserId + " has not been found.");
        }

        if (userRepository.getOptionalOfUserById(anotherUserId).isEmpty()) {
            throw new NotFoundException("User with ID " + anotherUserId + " has not been found.");
        }

        if (userRepository.getOptionalOfUserById(oneUserId).get().getFriends().isEmpty()
                || userRepository.getOptionalOfUserById(anotherUserId).get().getFriends().isEmpty()) {
            throw new ValidationException("Validation has failed, both or one of users friends list empty.");
        }

        if (!userRepository.getOptionalOfUserById(oneUserId).get().getFriends().contains(anotherUserId)
                || !userRepository.getOptionalOfUserById(anotherUserId).get().getFriends().contains(oneUserId)) {
            throw new ValidationException("Validation has failed, both or one of users is(are) not connected as friend(s).");
        }
    }

    @Override
    public void checkIsPossibleToAddLike(int filmId, int userId) {
        log.debug("Checking possibility to add like to film {} from user {}.", filmId, userId);

        if (filmRepository.getOptionalOfFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with id " + filmId + " has not been found.");
        }

        if (userRepository.getOptionalOfUserById(userId).isEmpty()) {
            throw new NotFoundException("User with id " + userId + " has not been found.");
        }

        if (filmRepository.getOptionalOfFilmById(filmId).get().getLikes().contains(userId)) {
            throw new ValidationException("This user already has set Like to Film " + filmId + ".");
        }
    }

    @Override
    public void checkIsPossibleToRemoveLike(int filmId, int userId) {
        log.debug("Checking possibility to remove like from film {} from user {}.", filmId, userId);

        if (filmRepository.getOptionalOfFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with id " + filmId + " has not been found.");
        }

        if (userRepository.getOptionalOfUserById(userId).isEmpty()) {
            throw new NotFoundException("User with id " + userId + " has not been found.");
        }

        if (!filmRepository.getOptionalOfFilmById(filmId).get().getLikes().contains(userId)) {
            throw new ValidationException("This user has not set Like to Film " + filmId + ".");
        }
    }
}
