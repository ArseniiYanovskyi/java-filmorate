package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FilmControllerTests {
	private FilmController filmController;

	@BeforeEach
	void resetController() {
		filmController = new FilmController();
	}

	@AfterEach
	void clearData() {
		filmController.deleteAllFilms();
	}

	@Test
	void shouldAddFilmCorrectly() {
		Film film = new Film();
		film.setRate(3);
		film.setName("FilmName");
		film.setDescription("FilmDescription");
		film.setReleaseDate(LocalDate.of(1999, 10, 6));
		film.setDuration(120);

		filmController.postFilm(film);

		List<Film> filmsList = new ArrayList<>();
		filmsList.add(film);

		Assertions.assertEquals(filmsList, filmController.getFilmsList());
		Assertions.assertEquals(film, filmController.getFilmsList().get(0));

		Film anotherFilm = new Film();
		anotherFilm.setRate(1);
		anotherFilm.setName("AnotherFilmName");
		anotherFilm.setDescription("AnotherFilmDescription");
		anotherFilm.setReleaseDate(LocalDate.of(2005, 12, 15));
		anotherFilm.setDuration(180);

		filmController.postFilm(anotherFilm);

		filmsList.add(anotherFilm);

		Assertions.assertEquals(filmsList, filmController.getFilmsList());
		Assertions.assertEquals(anotherFilm, filmController.getFilmsList().get(1));
	}

	@Test
	void shouldEditFilmCorrectly() {
		Film film = new Film();
		film.setRate(3);
		film.setName("FilmName");
		film.setDescription("FilmDescription");
		film.setReleaseDate(LocalDate.of(1999, 10, 6));
		film.setDuration(120);

		filmController.postFilm(film);

		String maxCharSeqForDesc = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";

		List<Film> oneFilmList = filmController.getFilmsList();

		Film anotherFilm = new Film();
		anotherFilm.setName("AnotherFilmName");
		anotherFilm.setDescription(maxCharSeqForDesc);
		anotherFilm.setReleaseDate(oneFilmList.get(0).getReleaseDate());
		anotherFilm.setDuration(oneFilmList.get(0).getDuration());
		anotherFilm.setRate(4);
		anotherFilm.setId(oneFilmList.get(0).getId());

		filmController.putFilm(anotherFilm);

		Assertions.assertEquals("AnotherFilmName", filmController.getFilmsList().get(0).getName());
		Assertions.assertEquals(maxCharSeqForDesc, filmController.getFilmsList().get(0).getDescription());
		Assertions.assertEquals(4, filmController.getFilmsList().get(0).getRate());

		anotherFilm.setId(789);
		Assertions.assertThrows(NotFoundException.class, () -> filmController.putFilm(anotherFilm));
	}

	@Test
	void shouldReturnErrorsForBadFilmValidation() {
		String aboveMaxCharSeq = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"
				+ "%";

		Film film = new Film();
		film.setRate(3);
		film.setName("FilmName");
		film.setDescription(aboveMaxCharSeq);
		film.setReleaseDate(LocalDate.of(1999, 10, 6));
		film.setDuration(120);

		Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));

		Assertions.assertEquals(new ArrayList<>(), filmController.getFilmsList());

		film.setDescription("NowDescriptionCorrect");
		film.setDuration(-10);

		Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));

		Assertions.assertEquals(new ArrayList<>(), filmController.getFilmsList());

		film.setDuration(120);
		film.setReleaseDate(LocalDate.of(1866, 10, 19));

		Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));

		Assertions.assertEquals(new ArrayList<>(), filmController.getFilmsList());

		film.setReleaseDate(LocalDate.of(1999, 10, 6));
		film.setName("");

		Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));

		Assertions.assertEquals(new ArrayList<>(), filmController.getFilmsList());
	}
}
