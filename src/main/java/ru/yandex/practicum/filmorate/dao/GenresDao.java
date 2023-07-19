package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenresDao {
    void updateFilmGenres(Film film);

    Set<Integer> getFilmGenresById(int filmId);

    Optional<Genre> getOptionalOfGenreById(int id);

    List<Genre> getAllGenres();
}
