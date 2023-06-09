package ru.yandex.practicum.filmorate.repositories;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmRepository implements FilmRepository {
    private final HashMap<Integer, Film> filmsData = new HashMap<>();
    private int filmIdCounter = 0;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmsData.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (!isFilmPresent(film)) {
            film.setId(++filmIdCounter);
            filmsData.put(film.getId(), film);
        } else {
            throw new NotFoundException("Film with this information already added to repository.");
        }
        return filmsData.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (isFilmPresent(film)) {
            filmsData.put(film.getId(), film);
        } else {
            throw new NotFoundException("This film does not present in database.");
        }
        return filmsData.get(film.getId());
    }

    @Override
    public List<Film> getTopFilms(int size) {
        return filmsData.values().stream()
                .sorted((film1, film2) -> {
                    int compareValue = film1.getLikes().size() - film2.getLikes().size();
                    return -1 * compareValue;
                })
                .limit(size).collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmsData.get(filmId).addLike(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        filmsData.get(filmId).removeLike(userId);
    }


    @Override
    public Optional<Film> getOptionalOfFilmById(int filmId) {
        return Optional.ofNullable(filmsData.get(filmId));
    }

    @Override
    public void clear() {
        filmsData.clear();
    }

    private boolean isFilmPresent(Film film) {
        return filmsData.containsKey(film.getId());
    }


}
