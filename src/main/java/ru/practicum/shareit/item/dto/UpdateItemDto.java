package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Valid
@Getter
@AllArgsConstructor
public class UpdateItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;
    @JsonIgnore
    private UserDto owner;
    @JsonIgnore
    private ItemRequestDto request;
}
