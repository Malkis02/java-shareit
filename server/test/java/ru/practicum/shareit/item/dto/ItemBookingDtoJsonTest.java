package java.ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemBookingDtoJsonTest {

    @Autowired
    private JacksonTester<ItemBookingDto> json;

    @Test
    void testSerialize() throws IOException {
        var bookingDto = new BookingShortDto(1L,1L, LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(2));
        var dto = new ItemBookingDto(1L,"Дрель","простая дрель",true,null,bookingDto,bookingDto);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(dto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(dto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(dto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(dto.getNextBooking().getBookerId().intValue());
    }

    @Test
    void testSerializeWithNull() throws IOException {
        var bookingDto = new BookingShortDto(1L,1L, LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(2));
        var dto = new ItemBookingDto(1L,"Дрель","простая дрель",true,null,bookingDto,null);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).doesNotHaveJsonPathValue("$.nextBooking");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(dto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(dto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(null);
    }
}
