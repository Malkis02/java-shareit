package java.ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;


    @Test
    void testSerialize() throws IOException {
        var dto = new CommentDto(1L,"Add comment from user1","Vasya",
                Timestamp.valueOf(LocalDateTime.now()));

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
    }

    @Test
    void testSerializeWithNull() throws IOException {
        var dto = new CommentDto(1L,null,"Vasya",
                Timestamp.valueOf(LocalDateTime.now()));

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
    }
}
