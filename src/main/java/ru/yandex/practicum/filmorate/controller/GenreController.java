package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class GenreController {
    private final FilmService filmService;
    private final Logger log;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
        this.log = LoggerFactory.getLogger("GenreController");
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.debug("Received request for all of film genres.");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable String id) {
        log.debug("Received request for data of film genre with ID {}.", id);

        int genreId = Integer.parseInt(id);

        return filmService.getGenreById(genreId);
    }

}