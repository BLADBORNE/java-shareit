package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestCreationDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        if (itemRequest.getItems() != null) {
            itemRequestDto.setItems(itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        }

        return itemRequestDto;
    }

    public static ResponseItemRequestCreationDto responseItemRequestCreationDto(ItemRequest itemRequest) {
        ResponseItemRequestCreationDto responseItemRequestCreationDto = new ResponseItemRequestCreationDto();

        responseItemRequestCreationDto.setId(itemRequest.getId());
        responseItemRequestCreationDto.setDescription(itemRequest.getDescription());
        responseItemRequestCreationDto.setCreated(itemRequest.getCreated());

        return responseItemRequestCreationDto;
    }

    public static ItemRequest toItemRequest(ItemRequestCreationDto itemRequestCreationDto, User user) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestCreationDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }
}