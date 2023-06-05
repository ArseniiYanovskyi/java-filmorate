package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repositories.FilmRepository;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    private FilmRepository filmRepository;

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

    @Override
    public Film updateFilm(Film film) {
        return filmRepository.updateFilm(film);
    }

    @Override
    public void clearRepository() {
        filmRepository.clear();
    }
}
