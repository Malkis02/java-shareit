package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserDto owner;

    private ItemRequestDto request;
}
