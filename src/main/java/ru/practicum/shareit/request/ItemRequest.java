package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    private int id;
    private String description;
    private UserDto requestor;
    private LocalTime created;
}
