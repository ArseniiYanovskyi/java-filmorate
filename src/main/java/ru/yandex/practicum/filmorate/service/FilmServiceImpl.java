package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repositories.FilmRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private final FilmRepository filmRepository;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger("FilmService");

    @Override
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmValidation(film);

        log.debug("Film ID {} Title: {} adding in progress.", film.getId(), film.getName());
        return filmRepository.addFilm(film);
    }

    public Film getRequiredFilmById(int id) {
        return filmRepository.getOptionalOfFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with this Id does not exist in repository."));
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmValidation(film);
        Film thisFilm = filmRepository.getOptionalOfFilmById(film.getId())
                .orElseThrow(() -> new NotFoundException("Film with this Id does not exist in repository."));

        log.debug("Film ID {} Title: {} updating in progress.", film.getId(), film.getName());
        return filmRepository.updateFilm(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmRepository.getOptionalOfFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with this Id does not exist in repository."));
        User user = userService.getRequiredUserById(userId);

        if (film.getLikes().contains(user.getId())) {
            throw new ValidationException("This user already has set Like to Film " + filmId + ".");
        }
        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = filmRepository.getOptionalOfFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with this Id does not exist in repository."));
        User user = userService.getRequiredUserById(userId);

        if (!film.getLikes().contains(user.getId())) {
            throw new ValidationException("This user has not set Like to Film " + filmId + ".");
        }
        filmRepository.removeLike(filmId, userId);
    }


    private void checkIdFilmForPresentsInRepository(int id) {
        Film film = filmRepository.getOptionalOfFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with Id: " + id + " does not exist in repository."));
    }

    @Override
    public List<Film> getTopFilms(int size) {
        return filmRepository.getTopFilms(size);
    }

    @Override
    public void checkFilmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Validation for film has failed. Incorrect film name, might be: lesser 1 char.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film name, might be: lesser 1 char.");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.debug("Validation for film has failed. Incorrect film description, might be: too long.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film description, might be: too long.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_DATE_OF_BIRTH)) {
            log.debug("Validation for film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film release date, might be: was before 28th of December 1985.");
        }
        if (film.getDuration() <= 0) {
            log.debug("Validation for film has failed. Incorrect film duration, might be: lesser than 1 point.");
            throw new ValidationException("Validation for adding film has failed. Incorrect film duration, might be: lesser than 1 point.");
        }
        log.debug("Validation for new film was successfully finished.");
    }

    @Override
    public void clearRepository() {
        filmRepository.clear();
    }
}
