package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.ItemBooking;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.exception.PermissionException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemBookingService bookingService;
    private final CommentService commentService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item createNewItem(ItemCreationDto item, int userId) {
        log.info("Полчуен запрос на создание вещи");

        User user = userService.getUserById(userId);

        if (item.getRequestId() != null) {

            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(item.getRequestId());

            if (itemRequest.isPresent()) {
                return itemRepository.save(ItemMapper.toItemWithRequest(item, user, itemRequest.get()));
            }

            log.info("Запрос с id = {} не найден", item.getRequestId());

            throw new NoSuchElementException("Запрос не найден");
        }

        return itemRepository.save(ItemMapper.toItemWithoutRequest(item, user));
    }

    @Override
    public Item updateItem(Item item, int itemId, int userId) {
        log.info("Полчуен запрос на обновление вещи с id = {} от пользователя с id = {}", itemId, userId);

        Item curItem = getItemById(itemId, userId);

        if (curItem.getOwner().getId().equals(userId)) {
            if (item.getName() != null && !item.getName().equals(curItem.getName())) {
                curItem.setName(item.getName());
            }

            if (item.getDescription() != null && !item.getDescription().equals(curItem.getDescription())) {
                curItem.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null && !item.getAvailable().equals(curItem.getAvailable())) {
                curItem.setAvailable(item.getAvailable());
            }

            itemRepository.save(curItem);

            return curItem;
        }

        log.warn("Не владелец пытаестя изменить объект");

        throw new PermissionException("Доступ запрещен, изменять объект может только владелец");
    }

    @Override
    public Item getItemById(int itemId, int userId) {
        log.info("Получен запрос на получение вещи с id = {}", itemId);

        userService.getUserById(userId);

        Item item = itemContainsCheck(itemId);

        addItemBookingForItem(item, userId);

        addCommentsToItem(item);

        return item;
    }

    @Override
    public Item getItemByIdForBookingAndComment(int itemId) {
        log.info("Получен запрос на получение вещи с id = {}", itemId);

        return itemContainsCheck(itemId);
    }

    @Override
    public List<Item> getUsersItems(int userId) {
        log.info("Получен запрос на отправление всех вещей пользователю с id = {}", userId);

        userService.getUserById(userId);

        return itemRepository.findByOwnerIdOrderById(userId).stream().peek(item -> {
                    addItemBookingForItem(item, userId);
                    addCommentsToItem(item);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsForSearch(int userId, String search) {
        log.info("Получен запрос на отправление всех доступных вещей по условию: - {} от пользователя с id = {}",
                search, userId);

        userService.getUserById(userId);

        if (search == null || search.isBlank()) {
            return List.of();
        }

        return itemRepository.findByAvailableIsTrueAndNameContainingIgnoreCaseOrAvailableIsTrueAndDescriptionContainingIgnoreCase(
                search, search);
    }

    private Item itemContainsCheck(int itemId) {
        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isPresent()) {
            return item.get();
        }

        log.warn("Отсутствует вещь с id = {}", itemId);

        throw new NoSuchElementException(String.format("Отсутствует вещь с id = %d", itemId));
    }

    private void addItemBookingForItem(Item item, int userId) {
        ItemBooking closestBooking = bookingService.getTheClosestBookingForItem(userId, item.getId());
        ItemBooking futureBooking = bookingService.getFutureBookingForItem(userId, item.getId());

        if (closestBooking != null) {
            item.setLastBooking(closestBooking);
        }

        if (futureBooking != null) {
            item.setNextBooking(futureBooking);
        }
    }

    @Override
    public List<Item> findByRequestIdIn(List<Integer> requestsId) {
        log.info("Получен запос на отправку всех вещей, созданных по запросам");

        return itemRepository.findByRequestIdIn(requestsId);
    }

    private void addCommentsToItem(Item item) {
        List<Comment> comments = commentService.getItemComments(item.getId());

        if (!comments.isEmpty()) {
            item.setComments(comments);
        }
    }
}