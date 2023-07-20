package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

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
public class UsersDBStorage implements UsersDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        final String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY, " +
                "from USERS ";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper());
    }

    @Override
    public User addUser(User user) {
        final String sqlQuery = "insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY) "
                + "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getBirthday().toString());
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        final String sqlQuery = "update USERS set " +
                "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "where USER_ID = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public Optional<User> getOptionalOfUserById(int id) {
        final String sqlQuery = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS where USER_ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, new UserRowMapper(), id);

        if (users.size() != 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    @Override
    public List<User> getFriendsList(int id) {
        final String sqlQuery = "select FRIEND_ID from FRIENDS where USER_ID = ? ";

        List<Integer> friendsId = new ArrayList<>(jdbcTemplate.query(sqlQuery,
                new IdRowMapper(), id));

        return friendsId.stream()
                .map(this::getOptionalOfUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(int oneUserId, int anotherUserId) {
        final String sqlQuery = "insert into FRIENDS (user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, oneUserId, anotherUserId);
    }

    @Override
    public List<User> getMutualFriends(int firstUserId, int secondUserId) {
        final String sqlQuery = "select FRIEND_ID from FRIENDS where USER_ID = ? " +
                "and FRIEND_ID in (select FRIEND_ID from FRIENDS where USER_ID = ?)";

        List<Integer> mutualFriendsId = new ArrayList<>(jdbcTemplate.query(sqlQuery,
                new IdRowMapper(), firstUserId, secondUserId));

        return mutualFriendsId.stream()
                .map(this::getOptionalOfUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(int oneUserId, int anotherUserId) {
        final String sqlQuery = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, oneUserId, anotherUserId);
    }

    private static class IdRowMapper implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("FRIEND_ID");
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getInt("USER_ID"),
                    rs.getString("EMAIL"),
                    rs.getString("LOGIN"),
                    rs.getString("NAME"),
                    rs.getDate("BIRTHDAY").toLocalDate());
        }
    }
}
