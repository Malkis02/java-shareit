package java.ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest({ItemRequestMapper.class, ItemRepositoryMapper.class,
        UserRepositoryMapper.class, CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        CommentMapper.class,})
public class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper mapper;


    @Test
    void toItemRequestDto() {
        var request = new ItemRequestEntity();
        request.setId(1L);
        request.setDescription("Хотел бы воспользоваться щёткой для обуви");

        var result = mapper.toItemRequestDto(request);
        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
    }

    @Test
    void toItemRequestEntity() {
        var request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("Хотел бы воспользоваться щёткой для обуви");

        var result = mapper.toItemRequestEntity(request,1L);
        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
    }
}
