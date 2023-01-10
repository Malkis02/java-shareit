package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String description;
    //private UserDto requestor;

    private LocalDateTime created;
}
