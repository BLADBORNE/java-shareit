package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBookerIdOrderByStartDesc(int bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(int bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(int bookerId, BookingStatus state, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(int bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int bookerId, LocalDateTime start,
                                                                              LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(int itemOwnerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(int itemOwnerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(int itemOwnerId, BookingStatus state, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(int itemOwnerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int itemOwnerId, LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    Optional<Booking> getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(int itemId, LocalDateTime now,
                                                                                             BookingStatus status);

    Optional<Booking> getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(int itemId, LocalDateTime now,
                                                                                       BookingStatus status);

    Optional<Booking> getFirstByBookerIdAndItemIdAndEndIsBefore(int userId, int itemId, LocalDateTime now);
}