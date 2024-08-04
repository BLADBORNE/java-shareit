package ru.practicum.shareit.request.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ItemRequestCreationDto {
    @NotBlank
    private String description;
}