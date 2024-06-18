package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createNewItem(Item item, int userId);

    Item updateItem(Item item, int itemId, int userId);

    Item getItemById(int itemId, int userId);

    Item getItemByIdForBookingAndComment(int itemId);

    List<Item> getUsersItems(int userId);

    List<Item> getItemsForSearch(int userId, String search);
}