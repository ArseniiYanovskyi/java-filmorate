package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository {
    public List<Film> getAll();
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public Film getFilmById(int id);
    public void clear();
}
