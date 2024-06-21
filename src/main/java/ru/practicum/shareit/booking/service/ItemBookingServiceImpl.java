package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemBookingServiceImpl implements ItemBookingService {
    private final BookingRepository bookingRepository;

    @Override
    public ItemBooking getTheClosestBookingForItem(int userId, int itemId) {
        log.info("Получен запрос на отправку ближайшего бронирования создателю с id = {}", userId);

        Optional<Booking> booking = bookingRepository
                .getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(itemId,
                        LocalDateTime.now(), BookingStatus.APPROVED);

        return bookingPermissionCheck(booking, userId);
    }

    @Override
    public ItemBooking getFutureBookingForItem(int userId, int itemId) {
        log.info("Получен запрос на отправку будущего бронирования создателю с id = {}", userId);

        Optional<Booking> booking = bookingRepository
                .getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(itemId,
                        LocalDateTime.now(), BookingStatus.APPROVED);

        return bookingPermissionCheck(booking, userId);
    }

    private ItemBooking bookingPermissionCheck(Optional<Booking> getBooking, int userId) {
        if (getBooking.isPresent()) {
            Booking booking = getBooking.get();
            if (!booking.getItem().getOwner().getId().equals(userId)) {
                log.warn("Ошибка доступа: у пользователя с id {} нет доступа к бронированию с id = {}, убираем даты...",
                        userId, booking.getId());

                return null;
            }
            return BookingMapper.toItemBooking(booking);
        }

        return null;
    }
}