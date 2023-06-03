package ru.yandex.practicum.filmorate.repositories;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryFilmRepository {
    private HashMap<Integer, Film> filmsData = new HashMap<>();

    public void addFilm(Film film){
        filmsData.put(film.getId(), film);
    }

    public void editExistingFilm(Film film){
        filmsData.put(film.getId(), film);
    }

    public Film getFilmById(int filmId){
        return filmsData.get(filmId);
    }

    public ArrayList<Film> getAllFilms(){
        return new ArrayList<>(filmsData.values());
    }

    public boolean isFilmPresent(Film film){
        if (filmsData.containsKey(film.getId())){
            return true;
        }
        return false;
    }
}
