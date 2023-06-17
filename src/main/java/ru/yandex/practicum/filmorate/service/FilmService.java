package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    List<Film> getAll();

    Film addFilm(Film film);

    Optional<Film> getOptionalOfRequiredFilmById(int id);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getTopFilms(int size);

    void clearRepository();
}
