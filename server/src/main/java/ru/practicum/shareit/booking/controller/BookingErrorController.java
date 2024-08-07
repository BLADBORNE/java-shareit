package ru.practicum.shareit.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.ChangeStatusException;
import ru.practicum.shareit.booking.exception.ItemUnavailableException;
import ru.practicum.shareit.booking.exception.PermissionException;
import ru.practicum.shareit.booking.exception.SelfReservationException;
import ru.practicum.shareit.item.model.ErrorResponse;

import java.util.NoSuchElementException;

@RestControllerAdvice(value = "ru.practicum.shareit.booking.controller")
public class BookingErrorController {
    @ExceptionHandler({ItemUnavailableException.class, ChangeStatusException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class, PermissionException.class, SelfReservationException.class})
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