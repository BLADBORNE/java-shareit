package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.ItemBookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemBookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemBookingServiceImpl itemBookingService;
    private Booking booking;

    @BeforeEach
    public void createBooking() {
        User owner = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();

        Item item = Item.builder()
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
    public void getTheClosestBookingForItem() {
        when(bookingRepository.getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(anyInt(),
                any(), any())).thenReturn(Optional.of(booking));

        ItemBooking itemBooking = itemBookingService.getTheClosestBookingForItem(1, 1);

        assertThat(itemBooking, is(notNullValue()));
        assertThat(itemBooking.getId(), is(1));
        assertThat(itemBooking.getBookerId(), is(2));
    }

    @Test
    public void getTheClosestBookingForItemIsNull() {
        when(bookingRepository.getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(anyInt(),
                any(), any())).thenReturn(Optional.empty());

        ItemBooking itemBooking = itemBookingService.getTheClosestBookingForItem(1, 1);

        assertNull(itemBooking);
    }

    @Test
    public void getTheClosestBookingForItemIsNullWhenPermissionDenied() {
        when(bookingRepository.getFirstByItemIdAndStartIsLessThanEqualAndStatusEqualsOrderByStartDesc(anyInt(),
                any(), any())).thenReturn(Optional.of(booking));

        ItemBooking itemBooking = itemBookingService.getTheClosestBookingForItem(2, 1);

        assertNull(itemBooking);
    }

    @Test
    public void getFutureBookingForItem() {
        when(bookingRepository.getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(anyInt(),
                any(), any())).thenReturn(Optional.of(booking));

        ItemBooking itemBooking = itemBookingService.getFutureBookingForItem(1, 1);

        assertThat(itemBooking, is(notNullValue()));
        assertThat(itemBooking.getId(), is(1));
        assertThat(itemBooking.getBookerId(), is(2));
    }

    @Test
    public void getFutureBookingForItemIsNull() {
        when(bookingRepository.getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(anyInt(),
                any(), any())).thenReturn(Optional.empty());

        ItemBooking itemBooking = itemBookingService.getFutureBookingForItem(1, 1);

        assertNull(itemBooking);
    }

    @Test
    public void getFutureBookingForItemIsNullWhenPermissionDenied() {
        when(bookingRepository.getFirstByItemIdAndStartIsGreaterThanAndStatusEqualsOrderByStart(anyInt(),
                any(), any())).thenReturn(Optional.of(booking));

        ItemBooking itemBooking = itemBookingService.getFutureBookingForItem(2, 1);

        assertNull(itemBooking);
    }
}