package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryMapper mapper;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toUserDto(userService.create(mapper.mapToUserEntity(user))));
    }

    @PatchMapping("{userId}")
    public ResponseEntity<UserDto> updateUser(@Min(1L) @PathVariable Long userId,
                                              @RequestBody UpdateUserDto user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.toUserDto(userService.update(mapper.mapToUserEntity(user),userId)));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUser(@Min(1L) @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toUserDto(userService.get(userId)));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAll().stream().map(mapper::toUserDto).collect(Collectors.toList()));
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@Min(1L) @PathVariable Long userId) {
        userService.delete(userId);
    }
}
