package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createNewItem(ItemCreationDto item, int userId);

    Item updateItem(Item item, int itemId, int userId);

    Item getItemById(int itemId, int userId);

    Item getItemByIdForBookingAndComment(int itemId);

    List<Item> getUsersItems(int userId);

    List<Item> getItemsForSearch(int userId, String search);

    List<Item> findByRequestIdIn(List<Integer> requestsId);
}