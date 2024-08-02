package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreationDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}