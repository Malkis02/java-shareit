package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingCreateRequestDto {

    private Long itemId;


    private LocalDateTime start;


    private LocalDateTime end;
}
