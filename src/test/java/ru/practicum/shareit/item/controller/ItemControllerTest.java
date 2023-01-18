package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({ItemController.class, ItemRequestMapper.class, ItemRepositoryMapper.class,
        UserRepositoryMapper.class, CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        CommentMapper.class,CommentRepository.class})
public class ItemControllerTest {

    private static final String PATH = "/items";

    private static final String PATH_WITH_ID = "/items/1";

    private static final String PATH_FOR_COMMENT = "/items/1/comment";

    private static final String PATH_FOR_SEARCH = "/items/search";

    private static final long ITEM_ID = 1L;

    private static final long USER_ID = 1L;

    private static final long COMMENT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepositoryMapper mapper;

    @MockBean
    private ItemService service;


    @Test
    void create() throws Exception {
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Простая дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        when(service.create(any(),eq(USER_ID))).thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/create.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("item/response/create.json")
                ));

    }

    @Test
    void update() throws Exception {
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        when(service.update(any(),eq(ITEM_ID))).thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("itemId",String.valueOf(ITEM_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/update.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("item/response/update.json")
                ));
    }

    @Test
    void get() throws Exception {
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        when(service.get(ITEM_ID,USER_ID)).thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("itemId",String.valueOf(ITEM_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/update.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("item/response/update.json")
                ));
    }

    @Test
    void getAll() throws Exception {
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        when(service.getAll(USER_ID)).thenReturn(List.of(item));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/all.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("item/response/all.json")
                ));
    }

    @Test
    void createComment() throws Exception {
        var user = new UserEntity(USER_ID,"Вася","vasya@yandex.ru");
        var item = new ItemEntity();
        var booking = new BookingEntity();
        var comment = new Comment();
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(9));
        booking.setStatus(BookingStatus.APPROVED);
        item.setId(ITEM_ID);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(user);
        item.setLastBooking(booking);
        comment.setId(COMMENT_ID);
        comment.setText("Add comment from user1");
        comment.setItem(item);
        comment.setAuthor(user);
        when(service.createComment(comment,ITEM_ID,USER_ID)).thenReturn(comment);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH_FOR_COMMENT)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/comment.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void search() throws Exception {
        String text = "Акк";
        var item = new ItemEntity();
        item.setId(ITEM_ID);
        item.setDescription("Аккумуляторная дрель");
        item.setName("Дрель");
        item.setAvailable(true);
        when(service.search(text)).thenReturn(Collections.singletonList(item));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_FOR_SEARCH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("text",text)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("item/request/all.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("item/response/all.json")
                ));
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
