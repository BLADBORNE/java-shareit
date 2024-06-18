package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody Item item) {
        ItemDto createdItem = ItemMapper.toItemDto(itemService.createNewItem(item, userId));

        log.info("Вещь с id = {} успешно создана", item.getId());

        return ResponseEntity.ok().body(createdItem).getBody();
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody Item item,
            @PathVariable(value = "itemId") Integer itemId
    ) {
        ItemDto updatedItem = ItemMapper.toItemDto(itemService.updateItem(item, itemId, userId));

        log.info("Пользователь с id = {} успешно обновил вещь с id = {}", userId, itemId);

        return ResponseEntity.ok().body(updatedItem).getBody();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(value = "itemId") Integer itemId) {
        ItemDto updatedItem = ItemMapper.toItemDto(itemService.getItemById(itemId, userId));

        log.info("Вещь с id = {} успешно отправлена клиенту", itemId);

        return ResponseEntity.ok().body(updatedItem).getBody();
    }

    @GetMapping
    public List<ItemDto> getUsersItemsDto(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemDto> usersItems = itemService.getUsersItems(userId).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Пользователю с id = {} отправлены все его вещи", userId);

        return ResponseEntity.ok().body(usersItems).getBody();
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsDtoForSearch(
            @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "text") String text
    ) {
        List<ItemDto> itemsDto = itemService.getItemsForSearch(userId, text).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Все доступные вещи по параметру отправлены клиенту");

        return ResponseEntity.ok().body(itemsDto).getBody();
    }
}