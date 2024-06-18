package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.CommentNotAllowedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBookingServiceImpl implements UserBookingService {
    private final BookingRepository bookingRepository;

    @Override
    public void commentCheck(int userId, int itemId) {
        log.info("Получен запрос на проверку возможности оставить отзыв пользователю с id = {} вещи с id = {}", userId,
                itemId);

        Booking booking = bookingRepository.getFirstByBookerIdAndItemIdAndEndIsBefore(userId, itemId,
                LocalDateTime.now());

        if (booking == null) {
            log.warn("Нельзя оставить отзыв пользователю с id = {} вещи с id = {}", userId, itemId);

            throw new CommentNotAllowedException("Сейчас нельзя оставить отзыв, одна из причин - текущее бронирование " +
                    "не окончено");
        }
    }
}