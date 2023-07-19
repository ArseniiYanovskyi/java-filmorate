package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class GenresDBStorage implements GenresDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenresDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateFilmGenres(Film film) {
        final String sqlQueryForDeleting = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleting, film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            final String sqlQueryForUpdating = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) values(?, ?)";
            jdbcTemplate.update(sqlQueryForUpdating, film.getId(), genre.getId());
        }
    }

    @Override
    public Set<Integer> getFilmGenresById(int filmId) {
        final String sqlQuery = "select GENRE_ID " +
                "from FILMS_GENRES " +
                "where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new IdRowMapper(), filmId));
    }

    @Override
    public Optional<Genre> getOptionalOfGenreById(int id) {
        final String sqlQuery = "select GENRE_ID, NAME from GENRES where GENRE_ID = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, new GenreRowMapper(), id);

        if (genres.size() != 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(genres.get(0));
    }

    @Override
    public List<Genre> getAllGenres() {
        final String sqlQuery = "select GENRE_ID, NAME from GENRES";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"));
        }
    }

    private static class IdRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("GENRE_ID");
        }
    }
}
