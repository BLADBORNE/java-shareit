package ru.practicum.shareit.booking.exception;

public class EndDateIsBeforeStartDateException extends RuntimeException {
    public EndDateIsBeforeStartDateException(String message) {
        super(message);
    }
}