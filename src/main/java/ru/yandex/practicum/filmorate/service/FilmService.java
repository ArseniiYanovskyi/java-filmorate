package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmService {
    List<Film> getAll();

    Film addFilm(Film film);

    Film getRequiredFilmById(int id);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getTopFilms(int size);

    Mpa getMpaById(int id);

    List<Mpa> getAllMpaData();

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    void checkFilmValidation(Film film);

    void checkMPAValidation(Film film);

    void checkGenresValidation(Film film);
}
