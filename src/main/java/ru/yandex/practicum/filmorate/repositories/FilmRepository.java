package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    List<Film> getAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(int size);

    Optional<Film> getOptionalOfFilmById(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    void clear();
}
