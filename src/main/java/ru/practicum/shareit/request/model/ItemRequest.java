package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemRequest {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}