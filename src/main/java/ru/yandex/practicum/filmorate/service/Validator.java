package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface Validator {
    void checkFilmValidation(Film film);

    void checkUserValidation(User user);

    void checkIsFilmPresent(int id);

    void checkIsUserPresent(int id);

    void checkIsPossibleToBecomeFriends(int oneUserId, int anotherUserId);

    void checkIsPossibleToBreakFriends(int oneUserId, int anotherUserId);

    void checkIsPossibleToAddLike(int filmId, int userId);

    void checkIsPossibleToRemoveLike(int filmId, int userId);
}
