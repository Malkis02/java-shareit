package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UpdateUserDto {

    private Long id;


    private String name;


    private String email;
}
