package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {
    private final Logger log;

    public ErrorHandler() {
        this.log = LoggerFactory.getLogger("ErrorHandler");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse errorResponse(NotFoundException e) {
        log.debug("Returning {} answer with message: {}", "NOT_FOUND_ERROR", e.getMessage());
        return new ErrorResponse("NOT_FOUND_ERROR", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorResponse(ValidationException e) {
        log.debug("Returning {} answer with message: {}", "BAD_REQUEST", e.getMessage());
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse errorResponse(RuntimeException e) {
        log.debug("Returning {} answer with message: {}", "INTERNAL_SERVER_ERROR", e.getMessage());
        return new ErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage());
    }
}
