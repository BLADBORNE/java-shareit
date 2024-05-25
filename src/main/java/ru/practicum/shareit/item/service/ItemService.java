package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createNewItem(Item item, int userId);

    ItemDto updateItem(Item item, int itemId, int userId);

    ItemDto getItemDtoById(int itemId, int userId);

    List<ItemDto> getUsersDtoItems(int userId);

    List<ItemDto> getItemsDtoForSearch(int userId, String search);
}