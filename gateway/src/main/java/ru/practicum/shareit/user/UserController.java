package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController  {


    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        return client.createUser(user);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                              @RequestBody UpdateUserDto user) {
        return client.updateUser(userId,user);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        return client.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return client.getAllUsers();
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return client.deleteUser(userId);
    }
}
