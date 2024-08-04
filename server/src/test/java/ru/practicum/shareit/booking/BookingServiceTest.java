package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Booking booking;
    private Item item;

    @BeforeEach
    public void createBooking() {
        User owner = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Дрель")
                .description("Красивая дрель")
                .available(true)
                .owner(owner)
                .build();

        User user = User.builder()
                .id(2)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .build();
    }

    @Test
    public void addBookingValid() {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(itemService.getItemByIdForBookingAndComment(anyInt())).thenReturn(item);

        when(bookingRepository.save(any())).thenReturn(booking);

        Booking getBooking = bookingService.addBooking(creationDto, anyInt());

        assertThat(booking, is(getBooking));
    }

    @Test
    public void shouldThrownItemUnavailableExceptionWhenAddBooking() {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        item.setAvailable(false);

        when(itemService.getItemByIdForBookingAndComment(anyInt())).thenReturn(item);

        assertThrows(ItemUnavailableException.class, () -> bookingService.addBooking(creationDto, anyInt()));
    }

    @Test
    public void shouldThrownSelfReservationExceptionWhenAddBooking() {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(itemService.getItemByIdForBookingAndComment(anyInt())).thenReturn(item);

        assertThrows(SelfReservationException.class, () -> bookingService.addBooking(creationDto, 1));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenAddBooking() {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> bookingService.addBooking(creationDto, anyInt()));
    }

    @Test
    public void shouldThrownExceptionIfItemDoesntExistsWhenAddBooking() {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(itemService.getItemByIdForBookingAndComment(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> bookingService.addBooking(creationDto, anyInt()));
    }

    @Test
    public void shouldApproveBooking() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        when(bookingRepository.save(any())).thenReturn(booking);

        Booking getBooking = bookingService.approveOrRejectBooking(1, anyInt(), true);

        assertThat(booking, is(getBooking));
    }

    @Test
    public void shouldRejectBooking() {
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        when(bookingRepository.save(any())).thenReturn(booking);

        Booking getBooking = bookingService.approveOrRejectBooking(1, anyInt(), false);

        assertThat(booking, is(getBooking));
    }

    @Test
    public void shouldThrownChangeStatusExceptionWhenApproveOrRejectBooking() {
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ChangeStatusException.class, () -> bookingService.approveOrRejectBooking(1, anyInt(),
                true));
    }

    @Test
    public void shouldThrownExceptionIfApproveOrRejectBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookingService.approveOrRejectBooking(1, anyInt(),
                true));
    }

    @Test
    public void shouldThrownPermissionExceptionIfApproveOrRejectBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(PermissionException.class, () -> bookingService.approveOrRejectBooking(2, anyInt(),
                true));
    }

    @Test
    public void shouldGetBookingById() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        Booking getBooking = bookingService.getBookingById(1, 1);

        assertThat(booking, is(getBooking));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetBookingById() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookingService.getBookingById(1, 1));
    }

    @Test
    public void shouldThrownPermissionExceptionWhenGetBookingById() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(PermissionException.class, () -> bookingService.getBookingById(1, anyInt()));
    }

    @Test
    public void shouldGetUserBookingsWithAllState() {
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1, BookingStatus.ALL, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetUserBookingsWithFutureState() {
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1, BookingStatus.FUTURE, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetUserBookingsWithPastState() {
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1, BookingStatus.PAST, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetUserBookingsWithCurrentState() {
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyInt(), any(), any(),
                any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1, BookingStatus.CURRENT, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetUserBookingsRejectedState() {
        when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getUserBookings(1, BookingStatus.REJECTED, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetUserBookings() {
        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> bookingService.getUserBookings(anyInt(),
                BookingStatus.ALL, 1, 1));
    }

    @Test
    public void shouldGetOwnerBookingsWithAllState() {
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getOwnerBookings(1, BookingStatus.ALL, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetOwnerBookingsWithFutureState() {
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getOwnerBookings(1, BookingStatus.FUTURE, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetOwnerBookingsWithPastState() {
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getOwnerBookings(1, BookingStatus.PAST, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetOwnerBookingsWithCurrentState() {
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyInt(), any(), any(),
                any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getOwnerBookings(1, BookingStatus.CURRENT, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldGetOwnerBookingsWithRejectedState() {
        when(bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(anyInt(), any(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> bookings = bookingService.getOwnerBookings(1, BookingStatus.REJECTED, anyInt(),
                1);

        assertThat(booking, is(bookings.get(0)));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetOwnerBookings() {
        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> bookingService.getOwnerBookings(anyInt(),
                BookingStatus.ALL, 1, 1));
    }
}