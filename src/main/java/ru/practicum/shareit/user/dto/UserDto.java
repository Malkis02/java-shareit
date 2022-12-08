package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
}
