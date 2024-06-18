package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static Booking toBooking(BookingCreationDto bookingDto, Item item, User user) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }

    public static ItemBooking toItemBooking(Booking booking) {
        ItemBooking itemBooking = new ItemBooking();

        itemBooking.setId(booking.getId());
        itemBooking.setBookerId(booking.getBooker().getId());

        return itemBooking;
    }

    public static ItemBookingDto toItemBookingDto(ItemBooking booking) {
        ItemBookingDto itemBookingDto = new ItemBookingDto();

        itemBookingDto.setId(booking.getId());
        itemBookingDto.setBookerId(booking.getBookerId());

        return itemBookingDto;
    }
}