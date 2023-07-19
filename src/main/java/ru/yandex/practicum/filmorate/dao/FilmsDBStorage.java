package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmsDBStorage implements FilmsDao{

    private final JdbcTemplate jdbcTemplate;
    private final GenresDao GenresRepository;
    @Autowired
    public FilmsDBStorage(JdbcTemplate jdbcTemplate, GenresDao GenresRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.GenresRepository = GenresRepository;
    }

    @Override
    public List<Film> getAll() {
        final String sqlQuery = "select FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, RELEASEDATE, DURATION, RATE, MPA, MPA_RATINGS.NAME "
                + "from FILMS "
                + "inner join MPA_RATINGS on FILMS.MPA = MPA_RATINGS.MPA_ID";

        List<Film> returningList = jdbcTemplate.query(sqlQuery, new FilmRowMapper());

        fillFilmListWithGenres(returningList);

        return returningList;
    }

    @Override
    public Film addFilm(Film film) {
        final String sqlQuery = "insert into FILMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, RATE, MPA) "
                + "values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setString(3, film.getReleaseDate().toString());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        final String sqlQuery = "update FILMS set " +
                    "NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATE = ?, MPA = ? " +
                    "where FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        return film;
    }

    @Override
    public List<Film> getTopFilms(int size) {
        final String sqlQuery = "select FILMS.FILM_ID " +
                "from FILMS " +
                "left outer join LIKES on FILMS.FILM_ID = LIKES.FILM_ID " +
                "group by FILMS.FILM_ID " +
                "order by COUNT(LIKES.USER_ID) desc " +
                "limit ?";

        List<Integer> topFilmsId = new ArrayList<>(jdbcTemplate.query(sqlQuery, new FilmIdRowMapper(), size));

        List<Film> returningList = topFilmsId.stream()
                .map(this::getOptionalOfFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        fillFilmListWithGenres(returningList);

        return returningList;
    }

    @Override
    public Optional<Film> getOptionalOfFilmById(int id) {
        final String sqlQuery = "select FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, RELEASEDATE, DURATION, RATE, MPA, MPA_RATINGS.NAME "
                + "from FILMS "
                + "inner join MPA_RATINGS on FILMS.MPA = MPA_RATINGS.MPA_ID "
                + "where FILM_ID = ?";

        final List<Film> films = jdbcTemplate.query(sqlQuery, new FilmRowMapper(), id);

        if (films.size() != 1) {
            return Optional.empty();
        }

        fillFilmListWithGenres(films);

        return Optional.ofNullable(films.get(0));
    }

    @Override
    public void addLike(int filmId, int userId) {
        final String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Integer> getLikes(int filmId) {
        final String sqlQuery = "select USER_ID " +
                "from LIKES " +
                "where FILM_ID = ?";
        return new ArrayList<>(jdbcTemplate.query(sqlQuery, new UserIdRowMapper(), filmId));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        final String sqlQuery = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private void fillFilmListWithGenres(List<Film> filmList) {
        for (Film film : filmList) {
            Set<Genre> filmGenres = new HashSet<>();
            for (Integer genreId : GenresRepository.getFilmGenresById(film.getId())){
                Genre genre = GenresRepository.getOptionalOfGenreById(genreId)
                        .orElseThrow(() -> new NotFoundException("Genre with Id: " + genreId + " does not exist in repository."));
                filmGenres.add(genre);
            }
            film.setGenres(filmGenres);
        }
    }

    private static class FilmIdRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("FILM_ID");
        }
    }

    private static class UserIdRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("USER_ID");
        }
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film(rs.getInt("FILM_ID"),
                    rs.getString("NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getDate("RELEASEDATE").toLocalDate(),
                    rs.getInt("DURATION"),
                    rs.getInt("RATE"));
            film.setMpa(new Mpa(rs.getInt("MPA"), rs.getString("MPA_RATINGS.NAME")));

            return film;
        }
    }

}
