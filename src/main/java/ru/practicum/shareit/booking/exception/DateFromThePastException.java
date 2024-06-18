package ru.practicum.shareit.booking.exception;

public class DateFromThePastException extends RuntimeException {
    public DateFromThePastException(String message) {
        super(message);
    }
}