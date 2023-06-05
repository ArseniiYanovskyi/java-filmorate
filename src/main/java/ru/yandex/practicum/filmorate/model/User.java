package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
public class User {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final User other = (User) obj;

        return this.email.equals(other.getEmail())
                && this.birthday.equals(other.getBirthday())
                && this.login.equals(other.getLogin())
                && this.name.equals(other.getName());
    }
}
