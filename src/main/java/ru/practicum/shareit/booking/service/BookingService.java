package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingCreationDto booking, int userId);

    Booking approveOrRejectBooking(int userId, int bookingId, Boolean approved);

    Booking getBookingById(int bookingId, int userId);

    List<Booking> getUserBookings(int userId, BookingStatus state);

    List<Booking> getOwnerBookings(int userId, BookingStatus state);
}