package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.DateFromThePastException;
import ru.practicum.shareit.booking.exception.EndDateIsBeforeStartDateException;
import ru.practicum.shareit.booking.exception.EndDateIsEqualsStartDateException;
import ru.practicum.shareit.booking.exception.UnsupportedBookingStatusException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody BookingCreationDto booking
    ) {
        pastDateCheck(booking.getStart(), booking.getEnd());

        endDateBeforeStartDateCheck(booking.getStart(), booking.getEnd());

        ensDateIsEqualsStartDateCheck(booking.getStart(), booking.getEnd());

        BookingDto bookingDto = BookingMapper.toBookingDto(bookingService.addBooking(booking, userId));

        log.info("Пользователь с id = {} успешно забронировал вещь с id = {}", bookingDto.getBooker(),
                bookingDto.getItem().getId());

        return ResponseEntity.ok().body(bookingDto).getBody();
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId,
            @RequestParam(value = "approved") Boolean isApproved
    ) {
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingService
                .approveOrRejectBooking(userId, bookingId, isApproved));

        log.info("Пользователь с id = {} успешно подтвердил бронирование с id = {}", userId, bookingId);

        return ResponseEntity.ok(bookingDto).getBody();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId
    ) {
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));

        log.info("Успешно отправлено клиенту бронирование с id = {}", bookingId);

        return ResponseEntity.ok(bookingDto).getBody();
    }

    @GetMapping
    public List<BookingDto> getUserDtoBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) Optional<String> state
    ) {
        List<BookingDto> bookingDtoList;

        if (state.isEmpty()) {
            bookingDtoList = getUsersBookingWithoutState(userId);
        } else {
            BookingStatus bookingStatus = convertToBookingStatusCheck(state.get());

            if (bookingStatus.equals(BookingStatus.ALL)) {
                bookingDtoList = getUsersBookingWithoutState(userId);
            } else if (bookingStatus.equals(BookingStatus.FUTURE)) {
                log.info("Успешно отправлены все будущие бронирования пользователю с id = {}", userId);

                bookingDtoList = bookingService.getUserBookings(userId, BookingStatus.FUTURE).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else if (bookingStatus.equals(BookingStatus.PAST)) {
                log.info("Успешно отправлены все предыдущие бронирования пользователю с id = {}", userId);

                bookingDtoList = bookingService.getUserBookings(userId, BookingStatus.PAST).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else if (bookingStatus.equals(BookingStatus.CURRENT)) {
                log.info("Успешно отправлены все текущие бронирования пользователю с id = {}", userId);

                bookingDtoList = bookingService.getUserBookings(userId, BookingStatus.CURRENT).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else {
                log.info("Успешно отправлены пользователю с id = {} все бронирования c параметром {}", userId,
                        state.get());

                bookingDtoList = bookingService.getUserBookings(userId, bookingStatus).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            }
        }

        return ResponseEntity.ok(bookingDtoList).getBody();
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) Optional<String> state
    ) {
        List<BookingDto> bookingDtoList;

        if (state.isEmpty()) {
            bookingDtoList = getOwnerBookingsWithoutState(userId);
        } else {
            BookingStatus bookingStatus = convertToBookingStatusCheck(state.get());

            if (bookingStatus.equals(BookingStatus.ALL)) {
                bookingDtoList = getOwnerBookingsWithoutState(userId);
            } else if (bookingStatus.equals(BookingStatus.FUTURE)) {
                log.info("Успешно отправлены все будущие бронирования создателю с id = {}", userId);

                bookingDtoList = bookingService.getOwnerBookings(userId, BookingStatus.FUTURE).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else if (bookingStatus.equals(BookingStatus.PAST)) {
                log.info("Успешно отправлены все предыдущие бронирования создателю с id = {}", userId);

                bookingDtoList = bookingService.getOwnerBookings(userId, BookingStatus.PAST).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else if (bookingStatus.equals(BookingStatus.CURRENT)) {
                log.info("Успешно отправлены все текущие бронирования создателю с id = {}", userId);

                bookingDtoList = bookingService.getOwnerBookings(userId, BookingStatus.CURRENT).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            } else {
                log.info("Успешно отправлены создателю с id = {} все бронирования c параметром {}", userId,
                        state.get());

                bookingDtoList = bookingService.getOwnerBookings(userId, bookingStatus).stream().map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
        }
        return ResponseEntity.ok().body(bookingDtoList).getBody();
    }

    private void pastDateCheck(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new DateFromThePastException("Начало или конец бронирования не могут быть в прошлом");
        }
    }

    private void endDateBeforeStartDateCheck(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new EndDateIsBeforeStartDateException("Бронирование не может закончиться раньше, чем началось");
        }
    }

    private void ensDateIsEqualsStartDateCheck(LocalDateTime start, LocalDateTime end) {
        if (end.isEqual(start)) {
            throw new EndDateIsEqualsStartDateException("Бронирование не может начаться и закончиться в одно и тоже время");
        }
    }

    private List<BookingDto> getUsersBookingWithoutState(int userId) {
        log.info("Успешно отправлены клиенту с id = {} все бронирования", userId);

        return bookingService.getUserBookings(userId, BookingStatus.ALL).stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<BookingDto> getOwnerBookingsWithoutState(int userId) {
        log.info("Успешно отправлены все бронирования создателю с id = {}", userId);

        return bookingService.getOwnerBookings(userId, BookingStatus.ALL).stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private BookingStatus convertToBookingStatusCheck(String status) {
        try {
            return BookingStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}