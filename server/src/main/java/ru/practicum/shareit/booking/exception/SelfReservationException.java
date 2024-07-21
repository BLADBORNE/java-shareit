package ru.practicum.shareit.booking.exception;

public class SelfReservationException extends RuntimeException {
    public SelfReservationException(String message) {
        super(message);
    }
}