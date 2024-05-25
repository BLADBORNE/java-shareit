package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto createNewItem(Item item, int userId) {
        return itemStorage.createNewItem(item, userId);
    }

    @Override
    public ItemDto updateItem(Item item, int itemId, int userId) {
        return itemStorage.updateItem(item, itemId, userId);
    }

    @Override
    public ItemDto getItemDtoById(int itemId, int userId) {
        return itemStorage.getItemDtoById(itemId, userId);
    }

    @Override
    public List<ItemDto> getUsersDtoItems(int userId) {
        return itemStorage.getUsersItemsDto(userId);
    }

    @Override
    public List<ItemDto> getItemsDtoForSearch(int userId, String search) {
        log.info("Получен запрос на отправление всех доступных вещей по условию: - {} от пользователя с id = {}",
                search, userId);

        userService.getUserDtoById(userId);

        if (search == null || search.isBlank()) {
            return List.of();
        }

        return itemStorage.getAllItemsDto().stream().filter(item -> (item.getName().toLowerCase()
                .contains(search.toLowerCase()) || item.getDescription().toLowerCase().contains(search
                .toLowerCase())) && item.getAvailable()).collect(Collectors.toList());
    }
}