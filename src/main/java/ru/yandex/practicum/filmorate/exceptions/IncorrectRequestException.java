package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectRequestException extends ResponseStatusException {
    public IncorrectRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public IncorrectRequestException(HttpStatus status, String reason) {
        super(status, reason);
    }
}
