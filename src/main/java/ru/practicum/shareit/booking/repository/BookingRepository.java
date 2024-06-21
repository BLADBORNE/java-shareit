package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
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

    Optional<Booking> getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(int itemId, LocalDateTime now,
                                                                              BookingStatus status);

    Optional<Booking> getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(int itemId, LocalDateTime now,
                                                                           BookingStatus status);

    Optional<Booking> getFirstByBookerIdAndItemIdAndEndIsBefore(int userId, int itemId, LocalDateTime now);
}