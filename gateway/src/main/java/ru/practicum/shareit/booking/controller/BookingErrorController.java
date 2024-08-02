package ru.practicum.shareit.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.DateFromThePastException;
import ru.practicum.shareit.booking.exception.EndDateIsBeforeStartDateException;
import ru.practicum.shareit.booking.exception.EndDateIsEqualsStartDateException;
import ru.practicum.shareit.booking.exception.UnsupportedBookingStatusException;
import ru.practicum.shareit.booking.model.ErrorResponse;

@RestControllerAdvice(value = "ru.practicum.shareit.booking.controller")
public class BookingErrorController {
    @ExceptionHandler({MethodArgumentNotValidException.class, DateFromThePastException.class,
            EndDateIsBeforeStartDateException.class, EndDateIsEqualsStartDateException.class,
            UnsupportedBookingStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}