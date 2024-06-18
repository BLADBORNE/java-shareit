package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.stream.Collectors;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.toUSerDto(item.getOwner()));

        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(item.getLastBooking()));
        }

        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(item.getNextBooking()));
        }

        if (item.getComments() != null) {
            itemDto.setComments(item.getComments().stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }

        return itemDto;
    }
}