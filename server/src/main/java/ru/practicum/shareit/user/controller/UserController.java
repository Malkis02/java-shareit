package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryMapper mapper;

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("поступил запрос на создание пользователя" + user);
        return mapper.toUserDto(userService.create(mapper.mapToUserEntity(user)));
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UpdateUserDto user) {
        return mapper.toUserDto(userService.update(mapper.mapToUserEntity(user), userId));
    }

    @GetMapping("{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return mapper.toUserDto(userService.get(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll().stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
