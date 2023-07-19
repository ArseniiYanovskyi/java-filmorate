package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MPARatingsDBStorage implements MPADao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPARatingsDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateMPAInformation(Film film) {
        final String sqlQueryForDeleting = "delete from FILMS_RATINGS where FILM_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleting, film.getId());

        final String sqlQueryForUpdating = "insert into FILMS_RATINGS (FILM_ID, MPA_ID) values(?, ?)";
        jdbcTemplate.update(sqlQueryForUpdating, film.getId(), film.getMpa().getId());
    }


    @Override
    public Optional<Mpa> getOptionalOfMpaById(int id) {
        final String sqlQuery = "select MPA_ID, NAME from MPA_RATINGS where MPA_ID = ?";
        final List<Mpa> mpaList = jdbcTemplate.query(sqlQuery, new MpaRowMapper(), id);

        if (mpaList.size() != 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(mpaList.get(0));
    }

    @Override
    public List<Mpa> getAllMpaData() {
        final String sqlQuery = "select MPA_ID, NAME from MPA_RATINGS";
        return jdbcTemplate.query(sqlQuery, new MpaRowMapper());
    }

    private static class MpaRowMapper implements RowMapper<Mpa> {

        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Mpa(rs.getInt("MPA_ID"), rs.getString("NAME"));
        }
    }
}
