package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class MPAController {
    private final FilmService filmService;
    private final Logger log;

    @Autowired
    public MPAController(FilmService filmService) {
        this.filmService = filmService;
        this.log = LoggerFactory.getLogger("MPAController");
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMPA() {
        log.debug("Received request for all of MPA.");
        return filmService.getAllMpaData();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMPAById(@PathVariable String id) {
        log.debug("Received request for data of MPA with ID {}.", id);

        int mpaId = Integer.parseInt(id);

        return filmService.getMpaById(mpaId);
    }
}
