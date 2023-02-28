package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingCreateRequestDto {
    @NotNull
    private Long itemId;


    private LocalDateTime start;


    private LocalDateTime end;
}
