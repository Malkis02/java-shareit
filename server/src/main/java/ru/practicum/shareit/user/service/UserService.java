package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity create(UserEntity user);

    UserEntity update(UserEntity user, Long userId);

    void delete(Long userId);

    UserEntity get(Long userId);

    List<UserEntity> getAll();
}
