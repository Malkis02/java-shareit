package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({BookingController.class, ItemRequestMapper.class, ItemRepositoryMapper.class,
        UserRepositoryMapper.class, CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        CommentMapper.class, CommentRepository.class})
public class BookingControllerTest {

    private static final String PATH = "/bookings";

    private static final String PATH_WITH_ID = "/bookings/1";

    private static final String PATH_FOR_BOOKING_OWNER = "/bookings/owner";


    private static final long BOOKING_ID = 1L;

    private static final long ITEM_ID = 1L;

    private static final long USER_ID = 1L;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepositoryMapper mapper;

    @MockBean
    private BookingService service;

    @Test
    void create() throws Exception {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        var booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setStart(LocalDateTime.of(2023,1,15,15,33,20));
        booking.setEnd(LocalDateTime.of(2023,1,16,15,33,20));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(userEntity);
        when(service.create(booking,userEntity.getId(),item.getId())).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("booking/request/create.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void get() throws Exception {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        var booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setStart(LocalDateTime.of(2023,1,15,15,33,20));
        booking.setEnd(LocalDateTime.of(2023,1,16,15,33,20));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(userEntity);
        when(service.get(booking.getId(),userEntity.getId())).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id",USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("booking/response/create.json")
                ));
    }

    @Test
    void getAll() throws Exception {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        var booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setStart(LocalDateTime.of(2023,1,15,15,33,20));
        booking.setEnd(LocalDateTime.of(2023,1,16,15,33,20));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(userEntity);
        int from = 0;
        int size = 20;
        when(service.getAll(userEntity.getId(), BookingState.ALL,from,size))
                .thenReturn(Collections.singletonList(booking));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("from",String.valueOf(from))
                        .param("size",String.valueOf(size)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("booking/response/all.json")
                ));
    }

    @Test
    void getAllOwnerItems() throws Exception {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        var booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setStart(LocalDateTime.of(2023,1,15,15,33,20));
        booking.setEnd(LocalDateTime.of(2023,1,16,15,33,20));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(userEntity);
        int from = 0;
        int size = 20;
        when(service.getAllOwnerItems(userEntity.getId(), BookingState.ALL,from,size))
                .thenReturn(Collections.singletonList(booking));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_FOR_BOOKING_OWNER)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("from",String.valueOf(from))
                        .param("size",String.valueOf(size)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("booking/response/all.json")
                ));
    }

    @Test
    void update() throws Exception {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Вася");
        userEntity.setEmail("vasya@yandex.ru");
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        var booking = new BookingEntity();
        booking.setId(BOOKING_ID);
        booking.setStart(LocalDateTime.of(2023,1,15,15,33,20));
        booking.setEnd(LocalDateTime.of(2023,1,16,15,33,20));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        booking.setBooker(userEntity);
        when(service.approve(booking.getId(),userEntity.getId(),true)).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("bookingId",String.valueOf(BOOKING_ID))
                        .queryParam("approved",String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("booking/request/update.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(getContentFromFile("booking/response/update.json")));
    }


    private String getContentFromFile(final String fileName) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + fileName).toPath(),
                    StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException();
        }
    }
}
