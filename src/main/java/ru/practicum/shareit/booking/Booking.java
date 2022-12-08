package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class Booking {
    private int id;
    private LocalTime start;
    private LocalTime end;
    private Item item;
    private User booker;
}
