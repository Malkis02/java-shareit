package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBookingDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Set<CommentDto> comments;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;
}
