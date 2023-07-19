package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmsDao {
    List<Film> getAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(int size);

    Optional<Film> getOptionalOfFilmById(int id);

    void addLike(int filmId, int userId);
    List<Integer> getLikes(int filmId);

    void removeLike(int filmId, int userId);

}
