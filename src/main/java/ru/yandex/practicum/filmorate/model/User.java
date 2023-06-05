package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
public class User {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
