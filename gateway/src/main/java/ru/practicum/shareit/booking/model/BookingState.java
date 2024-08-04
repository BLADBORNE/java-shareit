package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.exception.UnsupportedBookingStatusException;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static BookingState from(String stringState) {
        if (stringState == null) {
            return ALL;
        }

        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return state;
            }
        }

        throw new UnsupportedBookingStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}