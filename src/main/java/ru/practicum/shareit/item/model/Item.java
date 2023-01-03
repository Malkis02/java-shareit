package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.user.entity.UserEntity;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserEntity owner;

    private Set<CommentEntity> comments;

    private BookingEntity lastBooking;

    private BookingEntity nextBooking;

}
