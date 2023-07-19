package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmsDBStorage;
import ru.yandex.practicum.filmorate.dao.GenresDBStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JdbcTest
@Import({FilmsDBStorage.class, GenresDBStorage.class})
class FilmStorageTests {
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
        Set<Genre> first_film_genres = new HashSet<>();
        first_film_genres.add(new Genre(1, "Комедия"));
        first_film_genres.add(new Genre(2, "Драма"));
        film.setGenres(first_film_genres);

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
        Set<Genre> another_film_genres = new HashSet<>();
        another_film_genres.add(new Genre(4, "Триллер"));
        another_film_genres.add(new Genre(6, "Боевик"));
        anotherFilm.setGenres(another_film_genres);

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

        Assertions.assertEquals(new HashSet<>(), filmsStorage.getAll().get(0).getGenres());

        String maxCharSeqForDesc = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";

        List<Film> oneFilmList = filmsStorage.getAll();

        Film anotherFilm = new Film();
        anotherFilm.setName("AnotherFilmName");
        anotherFilm.setDescription(maxCharSeqForDesc);
        anotherFilm.setReleaseDate(oneFilmList.get(0).getReleaseDate());
        anotherFilm.setDuration(oneFilmList.get(0).getDuration());
        anotherFilm.setMpa(new Mpa(4, "R"));
        anotherFilm.setId(oneFilmList.get(0).getId());
        Set<Genre> anotherFilm_genres = new HashSet<>();
        anotherFilm_genres.add(new Genre(1, "Комедия"));
        anotherFilm_genres.add(new Genre(2, "Драма"));
        anotherFilm.setGenres(anotherFilm_genres);

        filmsStorage.updateFilm(anotherFilm);

        Assertions.assertEquals("AnotherFilmName", filmsStorage.getAll().get(0).getName());
        Assertions.assertEquals(maxCharSeqForDesc, filmsStorage.getAll().get(0).getDescription());
        Assertions.assertEquals(4, filmsStorage.getAll().get(0).getMpa().getId());
        Assertions.assertEquals("R", filmsStorage.getAll().get(0).getMpa().getName());
        Assertions.assertEquals(anotherFilm_genres, filmsStorage.getAll().get(0).getGenres());

        anotherFilm.setId(789);
        Assertions.assertThrows(NotFoundException.class, () -> filmsStorage.updateFilm(anotherFilm));
    }

    @Test
    void shouldReturnErrorsForBadFilmValidation() {
        String aboveMaxCharSeq = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
                + "%";

        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription(aboveMaxCharSeq);
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);

        Assertions.assertThrows(ValidationException.class, () -> filmsStorage.addFilm(film));

        film.setDescription("NowDescriptionCorrect");
        film.setDuration(-10);

        Assertions.assertThrows(ValidationException.class, () -> filmsStorage.addFilm(film));

        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(1866, 10, 19));

        Assertions.assertThrows(ValidationException.class, () -> filmsStorage.addFilm(film));

        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setName("");

        Assertions.assertThrows(ValidationException.class, () -> filmsStorage.addFilm(film));
    }

    @Test
    void shouldAddAndRemoveLikeCorrectly() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);
        Set<Genre> first_film_genres = new HashSet<>();
        first_film_genres.add(new Genre(1, "Комедия"));
        first_film_genres.add(new Genre(2, "Драма"));
        film.setGenres(first_film_genres);

        filmsStorage.addFilm(film);

        filmsStorage.addLike(1, 1);
        filmsStorage.addLike(1, 2);
        filmsStorage.addLike(1, 3);

        List<Integer> firstLikesForTestFilm = filmsStorage.getLikes(1);
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);

        Assertions.assertEquals(testList, filmsStorage.getLikes(1));

        filmsStorage.removeLike(1, 1);

        List<Integer> secondLikesForTestFilm = filmsStorage.getLikes(1);
        testList.remove(Integer.valueOf(1));

        Assertions.assertEquals(testList, filmsStorage.getLikes(1));
    }
}
