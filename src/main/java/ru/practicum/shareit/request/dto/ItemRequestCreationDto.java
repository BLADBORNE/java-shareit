package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestCreationDto {
    @NotBlank
    private String description;
}