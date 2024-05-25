package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto createNewItem(Item item, int userId);

    ItemDto updateItem(Item item, int itemId, int userId);

    ItemDto getItemDtoById(int itemId, int userId);

    Item getItemById(int itemId);

    List<ItemDto> getUsersItemsDto(int userId);

    List<ItemDto> getAllItemsDto();
}