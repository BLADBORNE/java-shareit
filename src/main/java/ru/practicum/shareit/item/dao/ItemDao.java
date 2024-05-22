package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemDao implements ItemStorage {
    private final UserStorage userStorage;
    private Map<Integer, Item> items = new HashMap<>();
    private int generatedItemId = 0;

    @Override
    public Item createNewItem(Item item, int userId) {
        log.info("Полчуен запрос на создание вещи");

        User user = userStorage.getUserById(userId);

        item.setId(++generatedItemId);
        item.setOwner(user);

        items.put(generatedItemId, item);

        log.info("Пользователь с id = {} успешно создан", item.getId());

        return item;
    }

    @Override
    public Item updateItem(Item item, int itemId, int userId) {
        log.info("Полчуен запрос на обновление вещи с id = {} от пользователя с id = {}", itemId, userId);

        Item curItem = items.get(itemId);

        userStorage.getUserById(userId);

        if (curItem.getOwner().getId() == userId) {
            if (item.getName() != null && !item.getName().equals(curItem.getName())) {
                curItem.setName(item.getName());
            }

            if (item.getDescription() != null && !item.getDescription().equals(curItem.getDescription())) {
                curItem.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null && !item.getAvailable().equals(curItem.getAvailable())) {
                curItem.setAvailable(item.getAvailable());
            }

            log.info("Пользователь с id = {} успешно обновил вещь с id = {}", userId, itemId);

            return curItem;
        }

        log.warn("Не владелец пытаестя изменить объект");

        throw new PermissionException("Доступ запрещен, изменять объект может только владелец");
    }

    @Override
    public Item getItemById(int itemId, int userId) {
        log.info("Получен запрос на получение вещи с id = {}", itemId);

        userStorage.getUserById(userId);

        if (items.containsKey(itemId)) {
            log.info("Вещь с id = {} успешно отправлена клиенту", itemId);

            return items.get(itemId);
        }

        throw new NoSuchElementException(String.format("Отсутствует вещь с id = %d", itemId));
    }

    @Override
    public List<Item> getUsersItems(int userId) {
        log.info("Получен запрос на отправление всех вещей пользователю с id = {}", userId);

        log.info("Пользователю с id = {} отправлены все его вещи", userId);

        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        log.info("Полчуен запрос на отправление всех вещей");

        log.info("Все вещи успешно отправлены клиенту");

        return new ArrayList<>(items.values());
    }
}