package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int rate;
    private List<Integer> likes = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Film other = (Film) obj;

        return this.name.equals(other.getName())
                && this.getReleaseDate().equals(other.getReleaseDate())
                && this.getDuration() == other.getDuration();
    }

    public void addLike(int id) {
        likes.add(id);
    }

    public void removeLike(int id) {
        if (likes.contains(id)) {
            likes.remove(Integer.valueOf(id));
        } else {
            throw new NotFoundException("Like with this userId not found.");
        }
    }
}
