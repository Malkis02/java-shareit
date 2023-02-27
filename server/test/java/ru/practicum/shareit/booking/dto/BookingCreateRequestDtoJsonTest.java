package java.ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingCreateRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateRequestDto> json;


    @Test
    void testSerialize() throws IOException {
        var dto = new BookingCreateRequestDto(1L,LocalDateTime.now().minusDays(1),LocalDateTime.now().plusDays(1));

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(dto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(dto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    void testSerializeWithNull() throws IOException {
        var dto = new BookingCreateRequestDto(1L,LocalDateTime.now().minusDays(1),null);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).doesNotHaveJsonPathValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(dto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
