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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.UpdateItem;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody ItemCreationDto item
    ) {
        return itemClient.createNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody UpdateItem item,
            @PathVariable(value = "itemId") Integer itemId
    ) {
        return itemClient.updateItem(userId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @PathVariable(value = "itemId") Integer itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getUsersItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsForSearch(
            @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "text") String text
    ) {
        return itemClient.getItemsForSearch(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(value = "itemId") Integer itemId,
            @Valid @RequestBody CreationCommentDto dto
    ) {
        return itemClient.addCommentToItem(userId, itemId, dto);
    }
}