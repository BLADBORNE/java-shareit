package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody BookingCreationDto booking
    ) {
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
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String status,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size
    ) {
        List<BookingDto> bookingDtoList = bookingService.getUserBookings(userId, status, from, size).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        log.info("Успешно отправлены все будущие бронирования пользователю с id = {}", userId);

        return ResponseEntity.ok().body(bookingDtoList).getBody();
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "state", required = false) String status,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Positive Integer size
    ) {
        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(userId, status, from, size).stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        log.info("Успешно отправлены бронирования создателю с id = {}", userId);

        return ResponseEntity.ok().body(bookingDtoList).getBody();
    }
}