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
@Import({FilmsDBStorage.class, LikesDBStorage.class})
class LikesTests {
    @Autowired
    private FilmsDBStorage filmsStorage;
    @Autowired
    private LikesDBStorage likesDBStorage;

    @Test
    void shouldAddAndDeleteLikeCorrectly() {
        Film film = new Film();
        film.setMpa(new Mpa(3, "PG-13"));
        film.setName("FilmName");
        film.setDescription("FilmDescription");
        film.setReleaseDate(LocalDate.of(1999, 10, 6));
        film.setDuration(120);

        filmsStorage.addFilm(film);

        List<Integer> likes = new ArrayList<>();

        Assertions.assertEquals(likes, likesDBStorage.getLikes(1));

        likesDBStorage.addLike(1, 99);
        likes.add(99);

        Assertions.assertEquals(likes, likesDBStorage.getLikes(1));

        likesDBStorage.removeLike(1, 99);
        likes.remove(0);

        Assertions.assertEquals(likes, likesDBStorage.getLikes(1));
   }

    @Test
    void shouldReturnCorrectTopList() {
        Film firstFilm = new Film();
        firstFilm.setMpa(new Mpa(3, "PG-13"));
        firstFilm.setName("FirstFilmName");
        firstFilm.setDescription("FirstFilmDescription");
        firstFilm.setReleaseDate(LocalDate.of(1999, 10, 6));
        firstFilm.setDuration(120);

        filmsStorage.addFilm(firstFilm);
        firstFilm.setId(1);

        Film secondFilm = new Film();
        secondFilm.setMpa(new Mpa(4, "R"));
        secondFilm.setName("SecondFilmName");
        secondFilm.setDescription("SecondFilmDescription");
        secondFilm.setReleaseDate(LocalDate.of(2000, 11, 7));
        secondFilm.setDuration(70);

        filmsStorage.addFilm(secondFilm);
        secondFilm.setId(2);

        Film thirdFilm = new Film();
        thirdFilm.setMpa(new Mpa(5, "NC-17"));
        thirdFilm.setName("ThirdFilmName");
        thirdFilm.setDescription("ThirdFilmDescription");
        thirdFilm.setReleaseDate(LocalDate.of(2001, 12, 8));
        thirdFilm.setDuration(150);

        filmsStorage.addFilm(thirdFilm);
        thirdFilm.setId(3);

        likesDBStorage.addLike(3, 10);
        likesDBStorage.addLike(3, 15);

        likesDBStorage.addLike(1, 20);

        Assertions.assertEquals(thirdFilm, filmsStorage.getTopFilms(3).get(0));
        Assertions.assertEquals(firstFilm, filmsStorage.getTopFilms(3).get(1));
        Assertions.assertEquals(secondFilm, filmsStorage.getTopFilms(3).get(2));
    }
}