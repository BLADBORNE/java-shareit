package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item createNewItem(Item item, int userId) {
        return itemStorage.createNewItem(item, userId);
    }

    @Override
    public Item updateItem(Item item, int itemId, int userId) {
        return itemStorage.updateItem(item, itemId, userId);
    }

    @Override
    public Item getItemById(int itemId, int userId) {
        return itemStorage.getItemById(itemId, userId);
    }

    @Override
    public List<Item> getUsersItems(int userId) {
        return itemStorage.getUsersItems(userId);
    }

    @Override
    public List<Item> getItemsForSearch(int userId, String search) {
        log.info("Получен запрос на отправление всех доступных вещей по условию: - {} от пользователя с id = {}",
                search, userId);

        userService.getUserById(userId);

        if (search == null || search.isBlank()) {
            return List.of();
        }

        return itemStorage.getAllItems().stream().filter(item -> (item.getName().toLowerCase()
                .contains(search.toLowerCase()) || item.getDescription().toLowerCase().contains(search
                .toLowerCase())) && item.getAvailable()).collect(Collectors.toList());
    }
}