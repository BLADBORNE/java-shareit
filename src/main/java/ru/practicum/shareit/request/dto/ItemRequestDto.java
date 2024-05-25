package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}