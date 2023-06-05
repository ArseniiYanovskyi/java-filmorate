package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    public List<Film> getAll();

    public Film addFilm(Film film);

    public Optional<Film> getOptionalOfRequiredFilmById(int id);

    public Film updateFilm(Film film);

    public void clearRepository();
}
