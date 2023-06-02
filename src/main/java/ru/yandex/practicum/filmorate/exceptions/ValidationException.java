package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ValidationException extends RuntimeException {
    private String message;

    public ValidationException (String message){
        this.message = message;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
