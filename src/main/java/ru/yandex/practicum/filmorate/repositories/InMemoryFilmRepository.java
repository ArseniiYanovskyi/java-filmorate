package ru.yandex.practicum.filmorate.repositories;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exceptions.IncorrectRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryFilmRepository {
    private HashMap<Integer, Film> filmsData = new HashMap<>();

    public void addFilm(Film film) {
        if (!isFilmPresent(film)) {
            filmsData.put(film.getId(), film);
        } else {
            throw new IncorrectRequestException("Film with this information already added to repository.");
        }
    }

    public void editExistingFilm(Film film) {
        if (isFilmPresent(film)) {
            filmsData.put(film.getId(), film);
        } else {
            throw new IncorrectRequestException(HttpStatus.NOT_FOUND, "This film does not present in database.");
        }
    }

    public Film getFilmById(int filmId) {
        return filmsData.get(filmId);
    }

    public ArrayList<Film> getAllFilms() {
        return new ArrayList<>(filmsData.values());
    }

    public boolean isFilmPresent(Film film) {
        if (filmsData.containsKey(film.getId())) {
            return true;
        }
        return false;
    }
}
