package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreationDto {
    @NotNull
    private Integer itemId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}