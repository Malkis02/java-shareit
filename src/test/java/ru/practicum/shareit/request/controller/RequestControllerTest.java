package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest({ItemRequestController.class, ItemRequestMapper.class, ItemRepositoryMapper.class,
        UserRepositoryMapper.class, CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        CommentMapper.class,})
public class RequestControllerTest {

    private static final String PATH = "/requests";

    private static final String PATH_WITH_ID = "/requests/1";

    private static final String PATH_FOR_GET_ALL = "/requests/all";

    private static final long REQUEST_ID = 1L;

    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRequestMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Test
    void create() throws Exception {
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(REQUEST_ID);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        when(service.create(any(),eq(USER_ID))).thenReturn(itemRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("itemRequest/response/request/create.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("itemRequest/response/create.json")
                ));

    }

    @Test
    void getAll() throws Exception {
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(REQUEST_ID);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");

        int from = 0;
        int size = 20;
        when(service.getAll(USER_ID,from,size)).thenReturn(Collections.singletonList(itemRequest));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_FOR_GET_ALL)
                        .header("X-Sharer-User-Id",USER_ID)
                        .param("from",String.valueOf(from))
                        .param("size",String.valueOf(size))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("itemRequest/response/all.json")
                ));

    }

    @Test
    void get() throws Exception {
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(REQUEST_ID);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        when(service.get(eq(USER_ID),eq(REQUEST_ID))).thenReturn(itemRequest);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id",USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("itemRequest/response/request/create.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("itemRequest/response/create.json")
                ));

    }

    @Test
    void getAllWithoutPage() throws Exception {
        var itemRequest = new ItemRequestEntity();
        itemRequest.setId(REQUEST_ID);
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");

        when(service.getAll(USER_ID)).thenReturn(Collections.singletonList(itemRequest));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id",USER_ID)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("itemRequest/response/all.json")
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
