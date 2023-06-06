package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    public List<Film> getAll();

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Optional<Film> getOptionalOfFilmById(int id);

    public void clear();
}
