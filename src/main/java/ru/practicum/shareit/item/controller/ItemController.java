package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item createNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody Item item) {
        return ResponseEntity.ok().body(itemService.createNewItem(item, userId)).getBody();
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody Item item,
            @PathVariable(value = "itemId") Integer itemId
    ) {
        return ResponseEntity.ok().body(itemService.updateItem(item, itemId, userId)).getBody();
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable(value = "itemId") Integer itemId) {
        return ResponseEntity.ok().body(itemService.getItemById(itemId, userId)).getBody();
    }

    @GetMapping
    public List<Item> getUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(itemService.getUsersItems(userId)).getBody();
    }

    @GetMapping("/search")
    public List<Item> getItemsForSearch(
            @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "text") String text
    ) {
        return ResponseEntity.ok().body(itemService.getItemsForSearch(userId, text)).getBody();
    }
}