package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private int id;
    private String description;
    private UserDto requestor;
    private LocalTime created;
}
