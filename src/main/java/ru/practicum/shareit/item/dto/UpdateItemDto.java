package ru.practicum.shareit.item.dto;

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

    private UserDto owner;

    private ItemRequestDto request;
}
