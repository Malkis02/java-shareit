package ru.practicum.shareit.user.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository repository;

    private UserService service;

    private UserRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        mapper = Mappers.getMapper(UserRepositoryMapper.class);
        service = new UserServiceImpl(repository, mapper);

        when(repository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void create() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");

        var result = service.create(user);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void update() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        when(repository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");


        var result = service.update(user, 1L);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void get() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        when(repository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        var result = service.get(userEntity.getId());
        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getName(), result.getName());
        assertEquals(userEntity.getEmail(), result.getEmail());
    }

    @Test
    void getAll() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        when(repository.findAll()).thenReturn(Collections.singletonList(userEntity));

        var result = service.getAll();
        assertNotNull(result);
        assertEquals(userEntity.getId(), result.get(0).getId());
        assertEquals(userEntity.getName(), result.get(0).getName());
        assertEquals(userEntity.getEmail(), result.get(0).getEmail());
    }

    @Test
    void delete() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        when(repository.save(userEntity)).thenReturn(userEntity);

        service.delete(userEntity.getId());
        var result = service.getAll();
        assertTrue(result.isEmpty());

    }

}
