package ru.practicum.shareit.booking.exception;

public class PermissionException extends RuntimeException {
    public PermissionException(String message) {
        super(message);
    }
}