package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repositories.InMemoryFilmRepository;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import java.time.LocalDate;
import java.util.List;

@RestController
public class FilmController {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;
    private final Logger log;

    public FilmController() {
        this.filmService = new FilmServiceImpl(new InMemoryFilmRepository());
        this.log = LoggerFactory.getLogger("FilmController");
    }

    @GetMapping("/films")
    public List<Film> getFilmsList() {
        log.debug("Received request for films list.");
        return filmService.getAll();
    }

    @PostMapping(value = "/films", consumes = {"application/json"})
    public Film postFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for adding film has failed.");
        }

        if (film.getId() != 0) {
            throw new ValidationException("Film should not contains ID to be added.");
        }

        log.debug("Film ID {} Title: {} adding in progress.", film.getId(), film.getName());
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films", consumes = {"application/json"})
    public Film putFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for editing film has failed.");
        }

        if (film.getId() == 0 || filmService.getOptionalOfRequiredFilmById(film.getId()).isEmpty()) {
            throw new IncorrectRequestException(HttpStatus.NOT_FOUND, "Film with this Id does not exist in repository.");
        }

        log.debug("Film ID {} Title: {} updating in progress.", film.getId(), film.getName());
        return filmService.updateFilm(film);
    }

    private boolean isFilmValid(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Validation for film has failed. Incorrect film name, might be: lesser 1 char.");
            return false;
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.debug("Validation for film has failed. Incorrect film description, might be: too long.");
            return false;
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_DATE_OF_BIRTH)) {
            log.debug("Validation for film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
            return false;
        }
        if (film.getDuration() <= 0) {
            log.debug("Validation for film has failed. Incorrect film duration, might be: lesser than 1 point.");
            return false;
        }
        log.debug("Validation for new film was successfully finished.");
        return true;
    }

    public void deleteAllFilms() {
        log.debug("Deleting all films data.");
        filmService.clearRepository();
    }
}
