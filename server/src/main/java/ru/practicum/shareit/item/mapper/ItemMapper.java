package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

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

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static Item toItemWithRequest(ItemCreationDto itemCreationDto, User user, ItemRequest request) {
        Item item = toItemWithoutRequest(itemCreationDto, user);

        item.setRequest(request);

        return item;
    }

    public static Item toItemWithoutRequest(ItemCreationDto itemCreationDto, User user) {
        Item item = new Item();

        item.setName(itemCreationDto.getName());
        item.setDescription(itemCreationDto.getDescription());
        item.setOwner(user);
        item.setAvailable(itemCreationDto.getAvailable());

        return item;
    }
}