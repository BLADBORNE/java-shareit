package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.CommentNotAllowedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.UserBookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private UserBookingServiceImpl userBookingService;
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
    public void shouldReturnOkResultWhenCommentCheck() {
        when(bookingRepository.getFirstByBookerIdAndItemIdAndEndIsBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(booking));

        userBookingService.commentCheck(1, 1);
    }

    @Test
    public void shouldThrownCommentNotAllowedExceptionWhenCommentCheck() {
        when(bookingRepository.getFirstByBookerIdAndItemIdAndEndIsBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.empty());

        assertThrows(CommentNotAllowedException.class, () -> userBookingService.commentCheck(1, 1));
    }
}
