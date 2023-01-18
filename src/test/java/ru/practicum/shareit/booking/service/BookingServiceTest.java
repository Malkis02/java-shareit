package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    private ItemRequestRepository requestRepository;

    private UserRepository userRepository;

    private UserService userService;

    private UserRepositoryMapper userMapper;

    private BookingService service;

    private ItemService itemService;

    private ItemRepository itemRepository;

    private ItemRepositoryMapper itemMapper;

    private BookingRepository repository;

    private CommentRepositoryMapper commentMapper;

    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        requestRepository = mock(ItemRequestRepository.class);
        repository = mock(BookingRepository.class);
        commentMapper = Mappers.getMapper(CommentRepositoryMapper.class);
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserRepositoryMapper.class);
        itemRepository = mock(ItemRepository.class);
        itemMapper = Mappers.getMapper(ItemRepositoryMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        itemService = new ItemServiceImpl(itemRepository,userService,repository,
                itemMapper,commentMapper,commentRepository);
        service = new BookingServiceImpl(repository,userService,itemService,itemRepository);

        when(repository.save(any())).then(invocation -> invocation.getArgument(0));
        when(requestRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(itemRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void create() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var itemResult = itemService.create(item,userResult.getId());
        var bookerResult = userService.create(booker);
        var resultRequest = service.create(booking,bookerResult.getId(),itemResult.getId());
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.getId());
        assertEquals(booking.getBooker(), resultRequest.getBooker());
        assertEquals(booking.getStatus(), resultRequest.getStatus());
        assertEquals(booking.getItem(), resultRequest.getItem());
    }

    @Test
    void createBookingWithIllegalTime() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now());
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var itemResult = itemService.create(item,userResult.getId());
        var bookerResult = userService.create(booker);
        IllegalTimeException thrown = assertThrows(IllegalTimeException.class, () -> {
            service.create(booking,bookerResult.getId(),itemResult.getId());
        });
        assertEquals("Некорректное время аренды",thrown.getMessage());
    }


    @Test
    void createBookingWithNotAvailableItem() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(false);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var itemResult = itemService.create(item,userResult.getId());
        var bookerResult = userService.create(booker);
        ItemNotAvailableException thrown = assertThrows(ItemNotAvailableException.class, () -> {
            service.create(booking,bookerResult.getId(),itemResult.getId());
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void createBookingWithOwner() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var itemResult = itemService.create(item,userResult.getId());
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.create(booking,userResult.getId(),itemResult.getId());
        });
        assertEquals("Нельзя брать в аренду у самого себя", thrown.getMessage());
    }

    @Test
    void createBookingWithItemIsBusy() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        var lastBooking = new BookingEntity();
        var nextBooking = new BookingEntity();
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        lastBooking.setStart(LocalDateTime.now().plusDays(1));
        lastBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(repository.findAllByItem(item)).thenReturn(List.of(lastBooking,nextBooking));

        var userResult = userService.create(user);
        var itemResult = itemService.create(item,userResult.getId());
        var bookerResult = userService.create(booker);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.create(booking,bookerResult.getId(),itemResult.getId());
        });
        assertEquals("Вещь занята на это время", thrown.getMessage());
    }

    @Test
    void get() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        var bookerResult = userService.create(booker);
        var resultRequest = service.get(booking.getId(),bookerResult.getId());
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.getId());
        assertEquals(booking.getBooker(), resultRequest.getBooker());
        assertEquals(booking.getStatus(), resultRequest.getStatus());
        assertEquals(booking.getItem(), resultRequest.getItem());
    }

    @Test
    void getWithNotFoundBooking() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var unknownUser = new UserEntity(3L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(user.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(userRepository.findById(unknownUser.getId())).thenReturn(Optional.of(unknownUser));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var unknownResult = userService.create(unknownUser);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.get(booking.getId(),unknownResult.getId());
        });
        assertEquals("Аренда не найдена", thrown.getMessage());
    }

    @Test
    void getAll() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findAllByBookerOrderByStartDesc(booker, PageRequest.of(from,size)))
                .thenReturn(Collections.singletonList(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        var resultRequest = service.getAll(booker.getId(), BookingState.ALL,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllWithCurrentStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        when(repository.findCurrentByBooker(any(),any(),any())).thenReturn(Collections.singletonList(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        var bookerResult = userService.create(booker);
        var resultRequest = service.getAll(bookerResult.getId(), BookingState.CURRENT,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }


    @Test
    void getAllWithPastStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        when(repository.findPastByBooker(any(),any(), any())).thenReturn(Collections.singletonList(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        var resultRequest = service.getAll(booker.getId(), BookingState.PAST,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllWithFutureStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        when(repository.findFutureByBooker(any(),any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        var resultRequest = service.getAll(booker.getId(), BookingState.FUTURE,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }



    @Test
    void getAllWithWaitingStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        when(repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.WAITING, PageRequest.of(from,size)))
                .thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        var resultRequest = service.getAll(booker.getId(), BookingState.WAITING,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllWithRejectedStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        when(repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.REJECTED, PageRequest.of(from,size)))
                .thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        var resultRequest = service.getAll(booker.getId(), BookingState.REJECTED,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllWithUnsupportedStatus() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        when(repository.findFutureByBooker(booker,LocalDateTime.now(), PageRequest.of(from,size)))
                .thenReturn(Collections.singletonList(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        userService.create(booker);
        UnsupportedStateException thrown = assertThrows(UnsupportedStateException.class, () -> {
            service.getAll(booker.getId(), BookingState.UNSUPPORTED_STATUS,from,size);
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void getAllForOwner() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findAllByOwnerItems(user, PageRequest.of(from,size))).thenReturn(Collections.singletonList(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.ALL,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerCurrent() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findCurrentByOwnerItems(any(),any(),any())).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        service.create(booking,booker.getId(),item.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.CURRENT,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerPast() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findPastByOwnerItems(any(),any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.PAST,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerFuture() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findFutureByOwnerItems(any(),any(), any())).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        service.create(booking,booker.getId(),item.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.FUTURE,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerWaiting() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findAllByOwnerItemsAndStatusOrderByStartDesc(user,BookingStatus.WAITING,PageRequest.of(from,size))).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.WAITING,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerRejected() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findAllByOwnerItemsAndStatusOrderByStartDesc(user,BookingStatus.REJECTED,PageRequest.of(from,size))).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        var resultRequest = service.getAllOwnerItems(user.getId(), BookingState.REJECTED,from,size);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.get(0).getId());
        assertEquals(booking.getBooker(), resultRequest.get(0).getBooker());
        assertEquals(booking.getStatus(), resultRequest.get(0).getStatus());
        assertEquals(booking.getItem(), resultRequest.get(0).getItem());
    }

    @Test
    void getAllForOwnerUnsupported() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        int from = 0;
        int size = 20;
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findAllByOwnerItemsAndStatusOrderByStartDesc(user,BookingStatus.REJECTED,PageRequest.of(from,size))).thenReturn(List.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        itemService.create(item,userResult.getId());
        UnsupportedStateException thrown = assertThrows(UnsupportedStateException.class, () -> {
            service.getAllOwnerItems(user.getId(), BookingState.UNSUPPORTED_STATUS,from,size);
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void approve() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var resultRequest = service.approve(booking.getId(),userResult.getId(),false);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.getId());
        assertEquals(booking.getBooker(), resultRequest.getBooker());
        assertEquals(resultRequest.getStatus(), BookingStatus.REJECTED);
        assertEquals(booking.getItem(), resultRequest.getItem());
    }

    @Test
    void approveWithTrue() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        var resultRequest = service.approve(booking.getId(),userResult.getId(),true);
        assertNotNull(resultRequest);
        assertEquals(booking.getId(), resultRequest.getId());
        assertEquals(booking.getBooker(), resultRequest.getBooker());
        assertEquals(resultRequest.getStatus(), BookingStatus.APPROVED);
        assertEquals(booking.getItem(), resultRequest.getItem());
    }

    @Test
    void approveWithStatusNotWaiting() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var userResult = userService.create(user);
        UnsupportedStatusException thrown = assertThrows(UnsupportedStatusException.class, () -> {
            service.approve(booking.getId(),userResult.getId(),true);
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void approveWithUserIsNotOwner() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var booker = new UserEntity(2L,"Вася Пупкин","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        var bookerResult = userService.create(booker);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.approve(booking.getId(),bookerResult.getId(),true);
        });
        assertNull(thrown.getMessage());
    }
}
