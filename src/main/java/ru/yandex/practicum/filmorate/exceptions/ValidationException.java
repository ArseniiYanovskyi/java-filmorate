package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ResponseStatusException {
    //мне бы хотелось заменить HttpStatus на 415, но с ним не проходят проверки в Postman =(
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
