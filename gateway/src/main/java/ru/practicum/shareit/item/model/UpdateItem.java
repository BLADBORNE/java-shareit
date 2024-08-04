package ru.practicum.shareit.item.model;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class UpdateItem {
    @Pattern(regexp = ".*[^ ].*")
    private String name;
    @Pattern(regexp = ".*[^ ].*")
    private String description;
    private Boolean available;
}