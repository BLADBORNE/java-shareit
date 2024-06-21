package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentDto> comments;
    // private ItemRequestDto request;
}