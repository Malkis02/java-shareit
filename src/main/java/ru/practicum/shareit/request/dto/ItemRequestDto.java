package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private int id;
    private String description;
    private UserEntity requestor;
    private LocalTime created;
}
