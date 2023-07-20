package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmsDBStorage;
import ru.yandex.practicum.filmorate.dao.LikesDBStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@Import(FilmsDBStorage.class)
class FilmsStorageTests {
    @Autowired
    private FilmsDBStorage filmsStorage;

    @Test
    void shouldAddFilmCorrectly() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);

        filmsStorage.addFilm(film);

        List<Film> filmsList = new ArrayList<>();
        filmsList.add(film);

        Assertions.assertEquals(filmsList, filmsStorage.getAll());
        Assertions.assertEquals(film, filmsStorage.getAll().get(0));

        Film anotherFilm = new Film();
        anotherFilm.setMpa(new Mpa(1, "G"));
        anotherFilm.setName("AnotherFilmName");
        anotherFilm.setDescription("AnotherFilmDescription");
        anotherFilm.setReleaseDate(LocalDate.of(2005, 12, 15));
        anotherFilm.setDuration(180);

        filmsStorage.addFilm(anotherFilm);

        filmsList.add(anotherFilm);

        Assertions.assertEquals(filmsList, filmsStorage.getAll());
        Assertions.assertEquals(anotherFilm, filmsStorage.getAll().get(1));
    }

    @Test
    void shouldEditFilmCorrectly() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);


        filmsStorage.addFilm(film);


        List<Film> oneFilmList = filmsStorage.getAll();

        Film anotherFilm = new Film();
        anotherFilm.setName("AnotherFilmName");
        anotherFilm.setDescription("AnotherFilmDescription");
        anotherFilm.setReleaseDate(oneFilmList.get(0).getReleaseDate());
        anotherFilm.setDuration(oneFilmList.get(0).getDuration());
        anotherFilm.setMpa(new Mpa(4, "R"));
        anotherFilm.setId(oneFilmList.get(0).getId());

        filmsStorage.updateFilm(anotherFilm);

        Assertions.assertEquals("AnotherFilmName", filmsStorage.getAll().get(0).getName());
        Assertions.assertEquals("AnotherFilmDescription", filmsStorage.getAll().get(0).getDescription());
        Assertions.assertEquals(4, filmsStorage.getAll().get(0).getMpa().getId());
        Assertions.assertEquals("R", filmsStorage.getAll().get(0).getMpa().getName());
    }

    @Test
    void shouldReturnCorrectlyById() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);


        filmsStorage.addFilm(film);

        Film anotherFilm = new Film();
        anotherFilm.setMpa(new Mpa(1, "G"));
        anotherFilm.setName("AnotherFilmName");
        anotherFilm.setDescription("AnotherFilmDescription");
        anotherFilm.setReleaseDate(LocalDate.of(2005, 12, 15));
        anotherFilm.setDuration(180);

        filmsStorage.addFilm(anotherFilm);

        Assertions.assertEquals(film, filmsStorage.getOptionalOfFilmById(1).get());
        Assertions.assertEquals(anotherFilm, filmsStorage.getOptionalOfFilmById(2).get());
    }

}
