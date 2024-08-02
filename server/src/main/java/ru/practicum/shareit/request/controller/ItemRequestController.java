package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestCreationDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseItemRequestCreationDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody ItemRequestCreationDto itemRequestCreationDto
    ) {
        ResponseItemRequestCreationDto response = ItemRequestMapper.responseItemRequestCreationDto(itemRequestService
                .addItemRequest(itemRequestCreationDto, userId));

        log.info("Пользователь с id = {} успешно создал запрос с описанием: {}", response.getId(),
                response.getDescription());

        return ResponseEntity.ok().body(response).getBody();
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(value = "itemRequestId") Integer id
    ) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestService
                .getItemRequestById(userId, id));

        log.info("Успешно отправлен запрос с id = {} пользователю с id = {}", id, userId);

        return ResponseEntity.ok().body(itemRequestDto).getBody();

    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());

        log.info("Успешно отправлены все запросы для вещей от пользователя с id = {}", userId);

        return ResponseEntity.ok().body(itemRequestDtos).getBody();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "from") Integer from,
            @RequestParam(value = "size") Integer size
    ) {
        List<ItemRequestDto> dtos = itemRequestService.getAllItemRequests(userId, from, size).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());

        log.info("Успешно отправлены {} запросов, начиная с {}, пользователю с id = {}", size, from, userId);

        return ResponseEntity.ok().body(dtos).getBody();
    }
}