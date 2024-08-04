package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    private Booking booking;
    private Item item;
    private BookingDto bookingDto;
    private final String headerUserId = "X-Sharer-User-Id";

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

        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    public void shouldCreateValidBooking() throws Exception {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(bookingService.addBooking(any(), anyInt())).thenReturn(booking);

        ResultActions resultActions = mockMvc.perform(post("/bookings")
                .header(headerUserId, item.getOwner().getId())
                .content(objectMapper.writeValueAsString(creationDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsOkCheck(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfRequestHeaderDoesntExistsWhenCreateBooking() throws Exception {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(bookingService.addBooking(any(), anyInt())).thenReturn(booking);

        ResultActions resultActions = mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(creationDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsBedRequestCheck(resultActions);
    }

    @Test
    public void shouldThrownServerError() throws Exception {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(bookingService.addBooking(any(), anyInt())).thenThrow(DataIntegrityViolationException.class);

        ResultActions resultActions = mockMvc.perform(post("/bookings")
                .header(headerUserId, item.getOwner().getId())
                .content(objectMapper.writeValueAsString(creationDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsServerErrorCheck(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenCreateBooking() throws Exception {
        BookingCreationDto creationDto = BookingCreationDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .build();

        when(bookingService.addBooking(any(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(post("/bookings")
                .header(headerUserId, item.getOwner().getId())
                .content(objectMapper.writeValueAsString(creationDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsNotFoundCheck(resultActions);
    }

    @Test
    public void shouldApproveOrRejectBooking() throws Exception {
        when(bookingService.approveOrRejectBooking(anyInt(), anyInt(), any())).thenReturn(booking);

        ResultActions resultActions = mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                .header(headerUserId, item.getOwner().getId())
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsOkCheck(resultActions);
    }

    @Test
    public void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt())).thenReturn(booking);

        ResultActions resultActions = mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                .header(headerUserId, item.getOwner().getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        bookingStatusIsOkCheck(resultActions);
    }

    @Test
    public void shouldGetOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyInt(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("state", "ALL");
        params.add("from", "0");
        params.add("size", "10");

        ResultActions resultActions = mockMvc.perform(get("/bookings/owner")
                .header(headerUserId, item.getOwner().getId())
                .params(params)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        listOfBookingStatusIsOkCheck(resultActions);
    }

    private void bookingStatusIsOkCheck(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((bookingDto))));
    }

    private void bookingStatusIsBedRequestCheck(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(400));
    }

    private void bookingStatusIsServerErrorCheck(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(500));
    }

    private void bookingStatusIsNotFoundCheck(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().is(404));
    }

    private void listOfBookingStatusIsOkCheck(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((List.of(bookingDto)))));
    }
}