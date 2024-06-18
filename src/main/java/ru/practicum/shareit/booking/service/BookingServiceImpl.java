package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.exception.ChangeStatusException;
import ru.practicum.shareit.booking.exception.ItemUnavailableException;
import ru.practicum.shareit.booking.exception.PermissionException;
import ru.practicum.shareit.booking.exception.SelfReservationException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking addBooking(BookingCreationDto booking, int userId) {
        log.info("Получен запрос на создание бронирования от пользователя с id = {} вещи с id = {}", userId,
                booking.getItemId());

        User user = userService.getUserById(userId);

        Item item = itemService.getItemByIdForBookingAndComment(booking.getItemId());

        if (!item.getOwner().getId().equals(userId)) {
            if (!item.getAvailable()) {
                throw new ItemUnavailableException("Нельзя забронировать недоступную вещь");
            }

            return bookingRepository.save(BookingMapper.toBooking(booking, item, user));
        }

        log.warn("Создатель с id = {} пытается создать бронирование со своей вещью", userId);

        throw new SelfReservationException("Создатель не может бронированить свою вещь");
    }

    @Override
    public Booking approveOrRejectBooking(int userId, int bookingId, Boolean approved) {
        log.info("Получен запрос на подтверждение бронирования с id = {} от пользователя с id = {}", bookingId, userId);

        Booking booking = getBookingById(bookingId, userId);

        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                if (approved.equals(true)) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }

                return bookingRepository.save(booking);
            }

            log.warn("Пользователь с id = {} пытается повторно изменить статус", userId);

            throw new ChangeStatusException("Нельзя повтоно изменить статус после подтверждения или отмены бронирования");
        }

        log.info("Бронирование пытаеся подтвердить не владелец с id = {}", userId);

        throw new PermissionException("Только создатель может подтверждать бронирование");
    }

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        log.info("Получен запрос на получение бронирования с id = {}", bookingId);

        userService.getUserById(userId);

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isPresent()) {
            Booking getBooking = booking.get();

            if (getBooking.getBooker().getId().equals(userId) || getBooking.getItem().getOwner().getId().equals(userId)) {
                return getBooking;
            }

            log.warn("Ошибка доступа: у пользователя с id {} нет доступа к бронированию с id = {}", userId, bookingId);

            throw new PermissionException("Только создатель и клиент могут посмотреть бронирование");
        }

        log.info("Отсутвует бронирование с id = {}", bookingId);

        throw new NoSuchElementException(String.format("Отсутвует бронирование с id = %d", bookingId));
    }

    @Override
    public List<Booking> getUserBookings(int userId, BookingStatus state) {
        List<Booking> bookings;

        if (BookingStatus.ALL.equals(state)) {
            log.info("Получен запрос на отправку всех бронирований пользовтелю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        } else if (BookingStatus.FUTURE.equals(state)) {
            log.info("Получен запрос на отправку всех будущих бронирований пользователю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
        } else if (BookingStatus.PAST.equals(state)) {
            log.info("Получен запрос на отправку всех прошлых бронирований пользователю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
        } else if (BookingStatus.CURRENT.equals(state)) {
            log.info("Получен запрос на отправку всех текущих бронирований пользователю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                    LocalDateTime.now(), LocalDateTime.now());
        } else {
            log.info("Получен запрос на отправку всех бронирований пользовтелю с id = {} с параметром {}", userId, state);

            userService.getUserById(userId);

            bookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(userId, state);
        }

        return bookings;
    }

    @Override
    public List<Booking> getOwnerBookings(int userId, BookingStatus state) {
        List<Booking> bookings;

        if (BookingStatus.ALL.equals(state)) {
            log.info("Получен запрос на отправку всех бронирований создателю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        } else if (BookingStatus.FUTURE.equals(state)) {
            log.info("Получен запрос на отправку всех будущих бронирований создателю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
        } else if (BookingStatus.PAST.equals(state)) {
            log.info("Получен запрос на отправку всех прошлых бронирований создателю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
        } else if (BookingStatus.CURRENT.equals(state)) {
            log.info("Получен запрос на отправку всех текущих бронирований создателю с id = {}", userId);

            userService.getUserById(userId);

            bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                    LocalDateTime.now(), LocalDateTime.now());
        } else {
            log.info("Получен запрос на отправку всех бронирований создателю с id = {} с параметром {}", userId, state);

            userService.getUserById(userId);

            bookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(userId, state);
        }

        return bookings;
    }
}