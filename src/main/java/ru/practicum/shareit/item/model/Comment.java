package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.entity.UserEntity;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;

    private String text;

    private ItemEntity item;

    private UserEntity author;

    private Timestamp created;
}
