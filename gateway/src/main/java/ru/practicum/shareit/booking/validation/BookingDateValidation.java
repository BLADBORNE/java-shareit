package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.exception.DateFromThePastException;
import ru.practicum.shareit.booking.exception.EndDateIsBeforeStartDateException;
import ru.practicum.shareit.booking.exception.EndDateIsEqualsStartDateException;

import java.time.LocalDateTime;

public class BookingDateValidation {
    private BookingDateValidation() {
    }

    public static void pastDateCheck(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new DateFromThePastException("Начало или конец бронирования не могут быть в прошлом");
        }
    }

    public static void endDateBeforeStartDateCheck(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new EndDateIsBeforeStartDateException("Бронирование не может закончиться раньше, чем началось");
        }
    }

    public static void ensDateIsEqualsStartDateCheck(LocalDateTime start, LocalDateTime end) {
        if (end.isEqual(start)) {
            throw new EndDateIsEqualsStartDateException("Бронирование не может начаться и закончиться в одно и тоже время");
        }
    }
}