package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public Item createNewItem(@RequestHeader("X-Sharer-User-Id") int userId, @Valid @RequestBody Item item) {
        return itemService.createNewItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody Item item,
            @PathVariable(value = "itemId") int itemId
    ) {
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable(value = "itemId") int itemId) {
        return itemService.getItemById(itemId, userId);

    }

    @GetMapping
    public List<Item> getUsersItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/search")
    public List<Item> getItemsForSearch(
            @RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(value = "text") String text
    ) {
        return itemService.getItemsForSearch(userId, text);
    }
}