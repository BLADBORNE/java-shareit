package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addItemRequest(ItemRequestCreationDto itemRequestCreationDto, int userId);

    List<ItemRequest> getUserItemRequests(int userId);

    List<ItemRequest> getAllItemRequests(int userId, int from, int size);

    ItemRequest getItemRequestById(int userId, int itemRequestId);
}