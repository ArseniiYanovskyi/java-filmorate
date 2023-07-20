package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikesDao {
    void addLike(int filmId, int userId);

    List<Integer> getLikes(int filmId);

    void removeLike(int filmId, int userId);
}
