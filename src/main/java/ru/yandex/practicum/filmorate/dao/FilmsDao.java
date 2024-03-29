package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmsDao {
    List<Film> getAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(int size);

    Optional<Film> getOptionalOfFilmById(int id);

}
