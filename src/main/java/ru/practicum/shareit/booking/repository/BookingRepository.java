package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(int bookerId, BookingStatus state);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int bookerId, LocalDateTime start,
                                                                              LocalDateTime end);

    List<Booking> findByItemOwnerIdOrderByStartDesc(int itemOwnerId);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(int itemOwnerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(int itemOwnerId, BookingStatus state);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(int itemOwnerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int itemOwnerId, LocalDateTime start,
                                                                                 LocalDateTime end);

    @Query(value = "SELECT * FROM bookings AS b WHERE b.item_id = ?1 AND b.start_date <= CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' ORDER BY b.start_date DESC LIMIT 1", nativeQuery = true)
    Booking getTheClosestBookingForItem(int itemId);

    @Query(value = "SELECT * FROM bookings AS b WHERE b.item_id = ?1 AND b.start_date > CURRENT_TIMESTAMP " +
            "AND b.status = 'APPROVED' ORDER BY b.start_date LIMIT 1", nativeQuery = true)
    Booking getFutureBookingForItem(int itemId);

    Booking getFirstByBookerIdAndItemIdAndEndIsBefore(int userId, int itemId, LocalDateTime now);
}