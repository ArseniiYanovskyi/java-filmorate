package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MPADao {
    void updateFilmsMPATable(Film film);

    Optional<Mpa> getOptionalOfMpaById(int id);

    List<Mpa> getAllMpaData();
}
