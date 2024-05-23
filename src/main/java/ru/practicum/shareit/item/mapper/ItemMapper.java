package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Mapper
public interface ItemMapper {
    ItemDto toItemDto(Item item);
}