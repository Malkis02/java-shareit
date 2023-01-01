package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private String text;

    private ItemDto item;

    private String authorName;

    private Timestamp created;
}
