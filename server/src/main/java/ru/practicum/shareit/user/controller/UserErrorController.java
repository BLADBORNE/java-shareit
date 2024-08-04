package ru.practicum.shareit.user.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.model.ErrorResponse;
import ru.practicum.shareit.user.exception.AlreadyExistException;

import java.util.NoSuchElementException;

@RestControllerAdvice(value = "ru.practicum.shareit.user.controller")
public class UserErrorController {
    @ExceptionHandler({AlreadyExistException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(final Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            return new ErrorResponse("Данный email занят, выберите другой");
        }

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}