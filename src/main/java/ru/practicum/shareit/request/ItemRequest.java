package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    private int id;
    private String description;
    private UserEntity requestor;
    private LocalTime created;
}
