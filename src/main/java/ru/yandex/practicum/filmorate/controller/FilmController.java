package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class FilmController {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private final FilmService filmService;
    private final UserService userService;
    private final Logger log;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
        this.log = LoggerFactory.getLogger("FilmController");
    }

    @GetMapping("/films")
    public List<Film> getFilmsList() {
        log.debug("Received request for films list.");
        return filmService.getAll();
    }

    @PostMapping(value = "/films", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.CREATED)
    public Film postFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for adding film has failed.");
        }

        log.debug("Film ID {} Title: {} adding in progress.", film.getId(), film.getName());
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.OK)
    public Film putFilm(@RequestBody Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Validation for editing film has failed.");
        }

        if (film.getId() == 0 || filmService.getOptionalOfRequiredFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Film with this Id does not exist in repository.");
        }

        log.debug("Film ID {} Title: {} updating in progress.", film.getId(), film.getName());
        return filmService.updateFilm(film);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") String count) {
        log.debug("Received request for top {} rated films.", count);
        return filmService.getTopFilms(Integer.parseInt(count));
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setUserLikeToFilm(@PathVariable String id, @PathVariable String userId) {
        int filmId = Integer.parseInt(id);
        int userIdentifier = Integer.parseInt(userId);

        log.debug("Received request for add new Like to film {} from user {}", filmId, userIdentifier);

        if (filmService.getOptionalOfRequiredFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with id " + filmId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(userIdentifier).isEmpty()) {
            throw new NotFoundException("User with id " + userIdentifier + " has not been found.");
        }

        if (filmService.getOptionalOfRequiredFilmById(filmId).get().getLikes().contains(userIdentifier)) {
            throw new ValidationException("This user already has set Like to Film " + filmId + ".");
        }

        filmService.addLike(filmId, userIdentifier);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteUserLikeToFilm(@PathVariable String id, @PathVariable String userId) {
        int filmId = Integer.parseInt(id);
        int userIdentifier = Integer.parseInt(userId);

        log.debug("Received request for remove Like to film {} from user {}", filmId, userIdentifier);

        if (filmService.getOptionalOfRequiredFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with id " + filmId + " has not been found.");
        }

        if (userService.getOptionalOfRequiredUserById(userIdentifier).isEmpty()) {
            throw new NotFoundException("User with id " + userIdentifier + " has not been found.");
        }

        if (!filmService.getOptionalOfRequiredFilmById(filmId).get().getLikes().contains(userIdentifier)) {
            throw new ValidationException("This user has not set Like to Film " + filmId + ".");
        }

        filmService.removeLike(filmId, userIdentifier);
    }

    @GetMapping(value = "/films/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Film getFilmById(@PathVariable String id) {
        int filmId = Integer.parseInt(id);

        log.debug("Received request to get film with ID {}", filmId);

        if (filmService.getOptionalOfRequiredFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Film with id " + filmId + " has not been found.");
        }

        return filmService.getOptionalOfRequiredFilmById(filmId).get();
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
