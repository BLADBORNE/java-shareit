package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.constraint.CreatedItem;
import ru.practicum.shareit.item.constraint.UpdatedItem;
import ru.practicum.shareit.item.dto.ItemCreationDto;
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
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @Validated(value = CreatedItem.class)
    @PostMapping
    public ItemDto createNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @Valid @RequestBody ItemCreationDto item
    ) {
        ItemDto createdItem = ItemMapper.toItemDto(itemService.createNewItem(item, userId));

        log.info("Вещь с id = {} успешно создана", createdItem.getId());

        return ResponseEntity.ok().body(createdItem).getBody();
    }

    @Validated(value = UpdatedItem.class)
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @Valid @RequestBody Item item,
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

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(value = "itemId") Integer itemId,
            @Valid @RequestBody CreationCommentDto dto
    ) {
        CommentDto comment = CommentMapper.toCommentDto(commentService.addCommentToItem(userId, itemId, dto));

        log.info("Пользователь с id = {} успешно добавил отзыв вещи с id = {}", userId, itemId);

        return ResponseEntity.ok().body(comment).getBody();
    }
}