package java.ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(UserRepositoryMapper.class)
public class UserMapperTest {

    @Autowired
    private UserRepositoryMapper mapper;

    @Test
    void toUserDto() {
        var user = new UserEntity(1L, "Вася", "vasya@yandex.ru");

        var result = mapper.toUserDto(user);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }
}
