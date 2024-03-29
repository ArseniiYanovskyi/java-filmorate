package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmsDao;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.dao.MPADao;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private static final LocalDate CINEMA_DATE_OF_BIRTH = LocalDate.of(1895, 12, 28);
    private final FilmsDao filmsRepository;
    private final GenresDao genresRepository;
    private final MPADao mpaRepository;
    private final LikesDao likesRepository;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger("FilmService");

    @Override
    public List<Film> getAll() {
        List<Film> returningList = filmsRepository.getAll();
        setGenresForFilmList(returningList);
        return returningList;
    }

    @Override
    public Film addFilm(Film film) {
        checkFilmValidation(film);
        checkMPAValidation(film);
        checkGenresValidation(film);

        log.debug("Film ID {} Title: {} adding in progress.", film.getId(), film.getName());

        Film addingFilm = filmsRepository.addFilm(film);
        genresRepository.updateFilmsGenresTable(addingFilm);
        mpaRepository.updateFilmsMPATable(addingFilm);
        return addingFilm;
    }

    public Film getRequiredFilmById(int id) {
        Film film = filmsRepository.getOptionalOfFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with this Id does not exist in repository."));
        List<Film> returningList = new ArrayList<>();
        returningList.add(film);
        setGenresForFilmList(returningList);
        return returningList.get(0);
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmValidation(film);
        checkMPAValidation(film);
        checkGenresValidation(film);

        checkIdFilmForPresentsInRepository(film.getId());

        log.debug("Film ID {} Title: {} updating in progress.", film.getId(), film.getName());

        genresRepository.updateFilmsGenresTable(film);
        mpaRepository.updateFilmsMPATable(film);
        return filmsRepository.updateFilm(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        checkIdFilmForPresentsInRepository(filmId);
        userService.checkIdUserForPresentsInRepository(userId);

        if (likesRepository.getLikes(filmId).contains(userId)) {
            throw new ValidationException("This user already has set Like to Film " + filmId + ".");
        }
        likesRepository.addLike(filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        checkIdFilmForPresentsInRepository(filmId);
        userService.checkIdUserForPresentsInRepository(userId);

        if (!likesRepository.getLikes(filmId).contains(userId)) {
            throw new ValidationException("This user has not set Like to Film " + filmId + ".");
        }
        likesRepository.removeLike(filmId, userId);
    }


    private void checkIdFilmForPresentsInRepository(int id) {
        Film film = filmsRepository.getOptionalOfFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with Id: " + id + " does not exist in repository."));
    }

    @Override
    public List<Film> getTopFilms(int size) {
        List<Film> returningList = filmsRepository.getTopFilms(size);
        setGenresForFilmList(returningList);
        return returningList;
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaRepository.getOptionalOfMpaById(id)
                .orElseThrow(() -> new NotFoundException("Mpa with Id: " + id + " does not exist in repository."));
    }

    @Override
    public List<Mpa> getAllMpaData() {
        return mpaRepository.getAllMpaData();
    }

    @Override
    public Genre getGenreById(int id) {
        return genresRepository.getOptionalOfGenreById(id)
                .orElseThrow(() -> new NotFoundException("Genre with Id: " + id + " does not exist in repository."));
    }

    @Override
    public List<Genre> getAllGenres() {
        return genresRepository.getAllGenres();
    }

    private void setGenresForFilmList(List<Film> filmList) {
        for (Film film : filmList) {
            Set<Genre> filmGenres = new HashSet<>();
            for (Integer genreId : genresRepository.getFilmGenresById(film.getId())) {
                Genre genre = genresRepository.getOptionalOfGenreById(genreId)
                        .orElseThrow(() -> new NotFoundException("Genre with Id: " + genreId + " does not exist in repository."));
                filmGenres.add(genre);
            }
            film.setGenres(filmGenres);
        }
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
    public void checkMPAValidation(Film film) {
        if (mpaRepository.getOptionalOfMpaById(film.getMpa().getId()).isEmpty()) {
            log.debug("Validation for film has failed. Incorrect Film MPA information.");
            throw new ValidationException("Validation for adding film has failed. Incorrect MPA ID.");
        }
        log.debug("Validation for new film MPA  was successfully finished.");
    }

    @Override
    public void checkGenresValidation(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            log.debug("Validation for new film Genres  was successfully finished."
                    + " Genres data for this film is empty");
            return;
        }

        List<Integer> filmGenresList = film.getGenres().stream()
                .map(Genre::getId)
                .filter(id -> genresRepository.getOptionalOfGenreById(id).isPresent())
                .collect(Collectors.toList());
        if (filmGenresList.size() != film.getGenres().size()) {
            log.debug("Validation for film has failed. Incorrect Film Genres information.");
            throw new ValidationException("Validation for adding film has failed. Incorrect Genres.");
        }
        log.debug("Validation for new film Genres  was successfully finished.");
    }
}
