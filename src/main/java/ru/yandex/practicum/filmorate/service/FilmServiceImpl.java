package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repositories.FilmRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;

    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film addFilm(Film film) {
        return filmRepository.addFilm(film);
    }

    public Optional<Film> getOptionalOfRequiredFilmById(int id) {
        return filmRepository.getOptionalOfFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmRepository.updateFilm(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmRepository.getOptionalOfFilmById(filmId).get();
        film.addLike(userId);
        filmRepository.updateFilm(film);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = filmRepository.getOptionalOfFilmById(filmId).get();
        film.removeLike(userId);
        filmRepository.updateFilm(film);
    }

    @Override
    public List<Film> getTopFilms(int size) {
        return filmRepository.getTopFilms(size);
    }

    @Override
    public void clearRepository() {
        filmRepository.clear();
    }
}
