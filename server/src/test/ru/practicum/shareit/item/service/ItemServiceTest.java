package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.CommentEntity;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    private ItemRequestRepository requestRepository;
    private UserRepository userRepository;

    private UserService userService;

    private UserRepositoryMapper userMapper;

    private ItemRepository repository;

    private ItemService service;

    private ItemRepositoryMapper mapper;

    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private BookingRepositoryMapper bookingMapper;

    private CommentRepository commentRepository;

    private CommentRepositoryMapper commentMapper;


    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserRepositoryMapper.class);
        userService = new UserServiceImpl(userRepository, userMapper);
        repository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = Mappers.getMapper(BookingRepositoryMapper.class);
        commentRepository = mock(CommentRepository.class);
        commentMapper = Mappers.getMapper(CommentRepositoryMapper.class);
        mapper = Mappers.getMapper(ItemRepositoryMapper.class);
        service = new ItemServiceImpl(repository,userService,bookingRepository,
                mapper,commentMapper,commentRepository,requestRepository);
        bookingService = new BookingServiceImpl(bookingRepository,userService,service,repository);

        when(repository.save(any())).then(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any())).then(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).then(invocation -> invocation.getArgument(0));
    }

    @Test
    void create() {
        var user = new UserEntity(1L,"Вася","vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(1L);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = userService.create(user);
        var resultRequest = service.create(mapper.mapToItemDto(item),result.getId());
        assertNotNull(result);
        assertNotNull(resultRequest);
        assertEquals(item.getId(), resultRequest.getId());
        assertEquals(item.getDescription(), resultRequest.getDescription());
        assertEquals(item.getOwner(), resultRequest.getOwner());
    }

    @Test
    void get() {
        var userEntity = new UserEntity();
        var item = new ItemEntity();
        var lastBooking = new BookingEntity();
        var nextBooking = new BookingEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        item.setId(1L);
        item.setDescription("Хотел бы воспользоваться щёткой для обуви");
        item.setOwner(userEntity);
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(10));
        lastBooking.setEnd(LocalDateTime.now().minusDays(9));
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setBooker(userEntity);
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().minusDays(10));
        nextBooking.setEnd(LocalDateTime.now().minusDays(9));
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setBooker(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItem(item)).thenReturn(List.of(lastBooking));


        var user = userService.create(userEntity);
        var result = service.get(user.getId(),item.getId());
        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void update() {
        var item = new ItemEntity();
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        item.setId(1L);
        item.setOwner(user);
        item.setName("Перфоратор");
        var itemEntity = new ItemEntity();
        itemEntity.setName("Дрель");
        itemEntity.setDescription("Аккумуляторная дрель");
        itemEntity.setId(2L);
        itemEntity.setOwner(user);
        itemEntity.setAvailable(true);
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(repository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        var result = service.update(item, itemEntity.getId());
        assertNotNull(result);
        assertEquals(itemEntity.getId(), result.getId());
        assertEquals(itemEntity.getName(), result.getName());
        assertEquals(itemEntity.getDescription(), result.getDescription());
    }

    @Test
    void updateWithItemWrongOwner() {
        var item = new ItemEntity();
        var owner = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var user = new UserEntity(2L, "Вася", "vasya@yandex.ru");
        item.setId(1L);
        item.setOwner(owner);
        item.setName("Перфоратор");
        var itemEntity = new ItemEntity();
        itemEntity.setName("Дрель");
        itemEntity.setDescription("Аккумуляторная дрель");
        itemEntity.setId(2L);
        itemEntity.setOwner(user);
        itemEntity.setAvailable(true);
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(repository.findById(itemEntity.getId())).thenReturn(Optional.of(itemEntity));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.update(itemEntity, item.getId());
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void getAll() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var itemEntity = new ItemEntity();
        var booking = new BookingEntity();
        booking.setStart(LocalDateTime.now());
        int from = 0;
        int size = 20;
        itemEntity.setOwner(user);
        itemEntity.setId(1L);
        itemEntity.setName("Дрель");
        itemEntity.setDescription("Аккумуляторная дрель");
        itemEntity.setAvailable(true);
        when(repository.findAllByOwnerId(user.getId(), PageRequest.of(from,size))).thenReturn(Collections.singletonList(itemEntity));
        when(bookingRepository.findAllByItem(itemEntity)).thenReturn(Collections.singletonList(booking));

        var result = service.getAll(user.getId(),from,size);
        assertNotNull(result);
        assertEquals(itemEntity.getId(), result.get(0).getId());
        assertEquals(itemEntity.getName(), result.get(0).getName());
        assertEquals(itemEntity.getDescription(), result.get(0).getDescription());
    }

    @Test
    void createComment() {
        var owner = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var booker = new UserEntity(2L, "Вася", "vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(1L);
        item.setName("Отвертка");
        item.setDescription("Аккумуляторная отвертка");
        item.setAvailable(true);
        item.setOwner(owner);
        var lastBooking = new BookingEntity();
        var comment = new CommentEntity();
        comment.setText("add comment");
        lastBooking.setBooker(booker);
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        var now = LocalDateTime.now();
        when(repository.save(item)).thenReturn(item);
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(bookingRepository.save(lastBooking)).thenReturn(lastBooking);
       when(bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(),any())).thenReturn(true);

        var result = service.createComment(commentMapper.toComment(comment,booker, item),
                item.getId(),booker.getId());
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    void createCommentWithEmptyText() {
        var owner = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var booker = new UserEntity(2L, "Вася", "vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(1L);
        item.setName("Отвертка");
        item.setDescription("Аккумуляторная отвертка");
        item.setAvailable(true);
        item.setOwner(owner);
        var lastBooking = new BookingEntity();
        var comment = new CommentEntity();
        comment.setText("");
        lastBooking.setBooker(booker);
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        var now = LocalDateTime.now();
        when(repository.save(item)).thenReturn(item);
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(bookingRepository.save(lastBooking)).thenReturn(lastBooking);
        when(bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(),any())).thenReturn(true);

        ItemNotAvailableException thrown = assertThrows(ItemNotAvailableException.class, () -> {
            service.createComment(commentMapper.toComment(comment,booker, item),
                    item.getId(),booker.getId());
        });
        assertNull(thrown.getMessage());
    }

    @Test
    void createCommentWithoutBooking() {
        var owner = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var booker = new UserEntity(2L, "Вася", "vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(1L);
        item.setName("Отвертка");
        item.setDescription("Аккумуляторная отвертка");
        item.setAvailable(true);
        item.setOwner(owner);
        var lastBooking = new BookingEntity();
        var comment = new CommentEntity();
        comment.setText("add comment");
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(1));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        var now = LocalDateTime.now();
        when(repository.save(item)).thenReturn(item);
        when(repository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(bookingRepository.save(lastBooking)).thenReturn(lastBooking);
        when(bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                item.getId(), booker.getId(), BookingStatus.APPROVED,now)).thenReturn(true);

        ItemNotAvailableException thrown = assertThrows(ItemNotAvailableException.class, () -> {
            service.createComment(commentMapper.toComment(comment,booker, item),
                    item.getId(),booker.getId());
        });
        assertNull(thrown.getMessage());
    }



    @Test
    void search() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var itemEntity = new ItemEntity();
        int from = 0;
        int size = 20;
        String text = "Акк";
        itemEntity.setOwner(user);
        itemEntity.setId(1L);
        itemEntity.setName("Дрель");
        itemEntity.setDescription("Аккумуляторная дрель");
        when(repository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                text,text,PageRequest.of(from,size))).thenReturn(Collections.singletonList(itemEntity));

        var result = service.search(text,from,size);
        assertNotNull(result);
        assertEquals(itemEntity.getId(), result.get(0).getId());
        assertEquals(itemEntity.getName(), result.get(0).getName());
        assertEquals(itemEntity.getDescription(), result.get(0).getDescription());
        assertEquals(itemEntity.getOwner(), result.get(0).getOwner());
    }

    @Test
    void searchWithEmptyText() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");
        var itemEntity = new ItemEntity();
        int from = 0;
        int size = 20;
        String text = "";
        itemEntity.setOwner(user);
        itemEntity.setId(1L);
        itemEntity.setName("Дрель");
        itemEntity.setDescription("Аккумуляторная дрель");
        when(repository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                text,text,PageRequest.of(from,size))).thenReturn(Collections.singletonList(itemEntity));

        var result = service.search(text,from,size);
        assertEquals(0,result.size());
    }
}
