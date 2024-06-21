package ru.practicum.shareit.booking.exception;

public class EndDateIsEqualsStartDateException extends RuntimeException {
    public EndDateIsEqualsStartDateException(String message) {
        super(message);
    }
}