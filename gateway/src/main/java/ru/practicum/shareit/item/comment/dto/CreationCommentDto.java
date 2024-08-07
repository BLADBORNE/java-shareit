package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreationCommentDto {
    @NotBlank
    String text;
}