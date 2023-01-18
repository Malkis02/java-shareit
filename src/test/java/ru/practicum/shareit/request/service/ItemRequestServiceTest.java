package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest {

    private ItemRequestRepository repository;

    private ItemRequestService service;

    private UserRepository userRepository;

    private UserService userService;

    private UserRepositoryMapper userMapper;

    private ItemRequestMapper mapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserRepositoryMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        repository = mock(ItemRequestRepository.class);
        mapper = Mappers.getMapper(ItemRequestMapper.class);
        service = new ItemRequestServiceImpl(repository,userService);

        when(userRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(repository.save(any())).then(invocation -> invocation.getArgument(0));

    }

    @Test
    void create() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(1L);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        var result = userService.create(user);
        var resultRequest = service.create(itemRequest,user.getId());
        assertNotNull(result);
        assertNotNull(resultRequest);
        assertEquals(itemRequest.getId(), resultRequest.getId());
        assertEquals(itemRequest.getDescription(), resultRequest.getDescription());
        assertEquals(itemRequest.getRequestor(), resultRequest.getRequestor());
    }

    @Test
    void get() {
        var userEntity = new UserEntity();
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(1L);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        itemRequest.setRequestor(userEntity);
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(repository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        userService.create(userEntity);
        var result = service.get(userEntity.getId(),itemRequest.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getRequestor(), result.getRequestor());
    }

    @Test
    void getAll() {
        var itemRequest = new ItemRequestEntity();
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        itemRequest.setId(1L);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        itemRequest.setRequestor(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(repository.findAllByRequestorIdOrderByCreatedDesc(userEntity.getId())).thenReturn(List.of(itemRequest));


        userService.create(userEntity);
        var result = service.getAll(userEntity.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.get(0).getId());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequest.getRequestor(), result.get(0).getRequestor());
    }

    @Test
    void getAllPageable() {
        var itemRequest = new ItemRequestEntity();
        var userEntity = new UserEntity();
        int from = 0;
        int size = 20;
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        itemRequest.setId(1L);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        itemRequest.setRequestor(userEntity);
        when(repository.findItemRequestEntitiesByRequestor_IdIsNot(userEntity.getId(), PageRequest.of(from,size)))
                .thenReturn(Collections.singletonList(itemRequest));

        userService.create(userEntity);
        var result = service.getAll(userEntity.getId(),from,size);
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.get(0).getId());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequest.getRequestor(), result.get(0).getRequestor());
    }
}
