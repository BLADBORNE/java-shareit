package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.ItemBooking;

public interface ItemBookingService {
    ItemBooking getTheClosestBookingForItem(int userId, int itemId);

    ItemBooking getFutureBookingForItem(int userId, int itemId);
}