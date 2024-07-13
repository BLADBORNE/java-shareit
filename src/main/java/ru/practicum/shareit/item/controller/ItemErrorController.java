package ru.practicum.shareit.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.CommentNotAllowedException;
import ru.practicum.shareit.item.exception.PermissionException;
import ru.practicum.shareit.item.model.ErrorResponse;

import java.util.NoSuchElementException;

@RestControllerAdvice(value = "ru.practicum.shareit.item.controller")
public class ItemErrorController {
    @ExceptionHandler({MethodArgumentNotValidException.class, PermissionException.class,
            CommentNotAllowedException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}