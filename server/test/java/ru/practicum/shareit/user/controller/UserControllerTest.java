package java.ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.service.UserService;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.nio.file.Files;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class, UserRepositoryMapper.class})
class UserControllerTest {

    private static final String PATH = "/users";

    private static final String PATH_WITH_ID = "/users/1";
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryMapper userMapper;

    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        UserEntity user = new UserEntity(USER_ID,"Вася","vasya@yandex.ru");
        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("src/test/resources/user/response/request/create.json"))
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("src/test/resources/user/response/create.json")
                ));

    }

    @Test
    void getAllUsers() throws Exception {
        UserEntity user = new UserEntity(USER_ID,"Вася","vasya@yandex.ru");
        when(userService.getAll()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("src/test/resources/user/response/all.json")
                ));

    }

    @Test
    void updateUser() throws Exception {
        UserEntity user = new UserEntity(USER_ID,"Вася Пупкин","vasya@yandex.ru");
        when(userService.update(any(),eq(USER_ID))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("src/test/resources/user/response/request/update.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("src/test/resources/user/response/update.json")
                ));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH_WITH_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).delete(USER_ID);
    }

    @Test
    void getUser() throws Exception {
        UserEntity user = new UserEntity(USER_ID,"Вася Пупкин","vasya@yandex.ru");
        when(userService.get(eq(USER_ID))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("src/test/resources/user/response/request/update.json"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("src/test/resources/user/response/update.json")
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
