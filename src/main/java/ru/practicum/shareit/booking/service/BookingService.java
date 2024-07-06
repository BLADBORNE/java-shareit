package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingCreationDto booking, int userId);

    Booking approveOrRejectBooking(int userId, int bookingId, Boolean approved);

    Booking getBookingById(int bookingId, int userId);

    List<Booking> getUserBookings(int userId, String status, int from, int size);

    List<Booking> getOwnerBookings(int userId, String status, int from, int size);

   // List<Booking> getUserPageableBookings(int userId, int from, int size);

    //List<Booking> getOwnerPageableBookings(int userId, int from, int size);
}