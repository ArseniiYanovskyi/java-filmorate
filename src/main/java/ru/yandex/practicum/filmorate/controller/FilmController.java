package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {
    private static final HashMap<Integer, Film> filmsData = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger("FilmController");

    @GetMapping("/films")
    public List<Film> getFilmsList(){
        log.debug("Received request for films list.");
        return new ArrayList<>(filmsData.values());
    }

    @PostMapping("/films")
    public Film postFilm(@Valid @RequestBody Film film){
        try{
            if(isFilmValid(film)) {
                filmsData.put(film.getId(), film);
                log.debug("Film ID {} Name {} added successfully.", film.getId(), film.getName());
                return filmsData.get(film.getId());
            }
        } catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        return film;
    }

    @PutMapping("/films")
    public Film putFilm(@RequestBody Film film){
        try{
            if(isFilmValid(film)) {
                filmsData.put(film.getId(), film);
                log.debug("Film ID {} Name {} added or edited successfully.", film.getId(), film.getName());
                return filmsData.get(film.getId());
            }
        } catch (ValidationException e){
            System.out.println(e.getMessage());
        }
        return film;
    }

    private boolean isFilmValid(Film film){
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Validation for film has failed. Incorrect film name, might be: lesser 1 char.");
            throw new ValidationException("Incorrect film name, might be: lesser 1 char.");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.debug("Validation for film has failed. Incorrect film description, might be: too long.");
            throw new ValidationException("Incorrect film description, might be: too long.");
        }
        if (film.getReleaseDate() == null || !isFilmReleaseDateValid(film.getReleaseDate())) {
            log.debug("Validation for film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
            throw new ValidationException("Incorrect film release date, might be: was before 28th of December 1985.");
        }
        if (film.getDuration() == 0 || film.getDuration() < 0) {
            log.debug("Validation for film has failed. Incorrect film duration, might be: lesser than 1 point.");
            throw new ValidationException("Incorrect film duration, might be: lesser than 1 point.");
        }
        log.debug("Validation for new film was successfully finished.");
        return true;
    }

    private boolean isFilmReleaseDateValid(String date){
        String[] splitDateLine = date.split("-");
        int year = Integer.parseInt(splitDateLine[0]);
        int month = Integer.parseInt(splitDateLine[1]);
        int day = Integer.parseInt(splitDateLine[2]);
        if(year < 1985 || (year == 1985 && month < 12) || (year == 1985 && month == 12 && day < 28)){
            return false;
        }
        return true;
    }
}
