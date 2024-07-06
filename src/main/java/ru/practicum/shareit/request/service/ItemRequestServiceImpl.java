package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequest addItemRequest(ItemRequestCreationDto itemRequestCreationDto, int userId) {
        log.info("Получен запрос на создание запроса от польователя с id = {} с описанием: {}", userId,
                itemRequestCreationDto.getDescription());

        User user = userService.getUserById(userId);

        return itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestCreationDto, user));
    }

    @Override
    public ItemRequest getItemRequestById(int userId, int itemRequestId) {
        log.info("Получен запрос на отправку запроса с id = {} от пользователя с id = {}", itemRequestId, userId);

        userService.getUserById(userId);

        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);

        if (itemRequest.isPresent()) {
            return setItemsForItemRequest(List.of(itemRequest.get())).get(0);
        }

        log.info("Запрос с id = {} не найден", userId);

        throw new NoSuchElementException("Запрос не найден");
    }

    @Override
    public List<ItemRequest> getUserItemRequests(int userId) {
        log.info("Получен запрос на отправку всех запросов пользователя с id = {}", userId);

        userService.getUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return setItemsForItemRequest(itemRequests);
    }

    @Override
    public List<ItemRequest> getAllItemRequests(int userId, int from, int size) {
        log.info("Полуен запрос на отправку {} запросов, начиная с {} от пользовтаеля с id = {}", size, from, userId);

        userService.getUserById(userId);

        return setItemsForItemRequest(itemRequestRepository.findByRequestorIdNot(userId, PageRequest
                .of(from > 0 ? from / size : 0, size, Sort.by("created").descending())).toList());

    }

    private List<ItemRequest> setItemsForItemRequest(List<ItemRequest> itemRequests) {
        List<Item> items = itemService.findByRequestIdIn(itemRequests.stream().map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream().peek(itemRequest -> {
            itemRequest.setItems(items.stream().filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                    .collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }
}