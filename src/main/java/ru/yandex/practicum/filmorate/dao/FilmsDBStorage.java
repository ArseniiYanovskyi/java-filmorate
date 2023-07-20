package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmsDBStorage implements FilmsDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        final String sqlQuery = "select FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, RELEASEDATE, DURATION, RATE, MPA, MPA_RATINGS.NAME "
                + "from FILMS "
                + "inner join MPA_RATINGS on FILMS.MPA = MPA_RATINGS.MPA_ID";

        return jdbcTemplate.query(sqlQuery, new FilmRowMapper());
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

        return topFilmsId.stream()
                .map(this::getOptionalOfFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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

        return Optional.ofNullable(films.get(0));
    }

    private static class FilmIdRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("FILM_ID");
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
