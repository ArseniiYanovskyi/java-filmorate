package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class FilmController {
    private final FilmService filmService;
    private final Logger log;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
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
        log.debug("Received request to add new film.");

        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films", consumes = {"application/json"})
    @ResponseStatus(code = HttpStatus.OK)
    public Film putFilm(@RequestBody Film film) {
        log.debug("Received request to edit existing film.");

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
        log.debug("Received request for add new Like to film {} from user {}", id, userId);

        int filmId = Integer.parseInt(id);
        int userIdentifier = Integer.parseInt(userId);

        filmService.addLike(filmId, userIdentifier);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteUserLikeToFilm(@PathVariable String id, @PathVariable String userId) {
        log.debug("Received request for remove Like to film {} from user {}", id, userId);

        int filmId = Integer.parseInt(id);
        int userIdentifier = Integer.parseInt(userId);

        filmService.removeLike(filmId, userIdentifier);
    }

    @GetMapping(value = "/films/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Film getFilmById(@PathVariable String id) {
        log.debug("Received request to get film with ID {}", id);

        int filmId = Integer.parseInt(id);

        return filmService.getRequiredFilmById(filmId);
    }
}
