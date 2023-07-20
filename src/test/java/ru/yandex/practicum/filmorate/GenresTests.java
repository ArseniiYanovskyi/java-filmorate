package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmsDBStorage;
import ru.yandex.practicum.filmorate.dao.GenresDBStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@JdbcTest
@Import(GenresDBStorage.class)
class GenresTests {
    @Autowired
    private GenresDBStorage genresDBStorage;

    @Test
    void shouldAddAndUpdateFilmGenreCorrectly() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);
        film.setId(1);

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(3, "Мультфильм"));
        genres.add(new Genre(5, "Документальный"));
        film.setGenres(genres);

        genresDBStorage.updateFilmsGenresTable(film);

        Set<Integer> filmGenresIds = new HashSet<>();
        filmGenresIds.add(3);
        filmGenresIds.add(5);

        Assertions.assertEquals(filmGenresIds, genresDBStorage.getFilmGenresById(1));

        genres.add(new Genre(2, "Драма"));
        film.setGenres(genres);

        genresDBStorage.updateFilmsGenresTable(film);

        filmGenresIds.add(2);
        Assertions.assertEquals(filmGenresIds, genresDBStorage.getFilmGenresById(1));

        genres.remove(0);film.setGenres(genres);

        genresDBStorage.updateFilmsGenresTable(film);

        filmGenresIds.remove(0);
        Assertions.assertEquals(filmGenresIds, genresDBStorage.getFilmGenresById(1));
    }

    @Test
    void shouldReturnCorrectGenreById() {
        Assertions.assertEquals(new Genre(1, "Комедия"),
                genresDBStorage.getOptionalOfGenreById(1).get());
        Assertions.assertEquals(new Genre(2, "Драма"),
                genresDBStorage.getOptionalOfGenreById(2).get());
        Assertions.assertEquals(new Genre(3, "Мультфильм"),
                genresDBStorage.getOptionalOfGenreById(3).get());
        Assertions.assertEquals(new Genre(4, "Триллер"),
                genresDBStorage.getOptionalOfGenreById(4).get());
        Assertions.assertEquals(new Genre(5, "Документальный"),
                genresDBStorage.getOptionalOfGenreById(5).get());
        Assertions.assertEquals(new Genre(6, "Боевик"),
                genresDBStorage.getOptionalOfGenreById(6).get());
    }
}