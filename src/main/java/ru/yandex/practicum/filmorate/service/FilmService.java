package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public List<Film> getAll();

    public Film addFilm(Film film);


    public Film updateFilm(Film film);

    public void clearRepository();
}
