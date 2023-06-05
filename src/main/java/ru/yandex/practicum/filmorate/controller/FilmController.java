package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repositories.InMemoryFilmRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
public class FilmController {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private static final InMemoryFilmRepository filmRepository = new InMemoryFilmRepository();

    private static final Logger log = LoggerFactory.getLogger("FilmController");

    @GetMapping("/films")
    public List<Film> getFilmsList() {
        log.debug("Received request for films list.");
        return filmRepository.getAllFilms();
    }

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for adding film has failed.");
        }
        filmRepository.addFilm(film);
        log.debug("Film ID {} Title: {} added successfully.", film.getId(), film.getName());
        return filmRepository.getFilmById(film.getId());
    }

    @PutMapping("/films")
    public Film putFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for editing film has failed.");
        }

        filmRepository.editExistingFilm(film);
        log.debug("Film ID {} Title: {} edited successfully.", film.getId(), film.getName());
        return filmRepository.getFilmById(film.getId());
    }

    private boolean isFilmValid(Film film) {
        if (film.getId() == 0) {
            log.debug("Setting new id for film.");
            film.setId(filmRepository.getAllFilms().size() + 1);
        }
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
}
