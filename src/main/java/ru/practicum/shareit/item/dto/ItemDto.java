package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}