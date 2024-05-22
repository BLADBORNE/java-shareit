package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createNewItem(Item item, int userId);

    Item updateItem(Item item, int itemId, int userId);

    Item getItemById(int itemId, int userId);

    List<Item> getUsersItems(int userId);

    List<Item> getAllItems();
}