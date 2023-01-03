package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        uses = {
                UserRepositoryMapper.class,
                ItemRepositoryMapper.class,
                BookingMapper.class,
                ItemMapper.class,
                UserMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingRepositoryMapper {
    @Mapping(target = "start",source = "start",qualifiedByName = "convertToLocalDateTime")
    @Mapping(target = "end",source = "end",qualifiedByName = "convertToLocalDateTime")
    Booking toBooking(BookingEntity entity);

    @Mapping(target = "id",source = "id")
    @Mapping(target = "booker.id",source = "bookerId")
    @Mapping(target = "start",source = "start",qualifiedByName = "convertToTimestamp")
    @Mapping(target = "end",source = "end",qualifiedByName = "convertToTimestamp")
    BookingEntity mapToEntity(BookingShortDto bookingShortDto);

    @Mapping(target = "id",source = "id")
    @Mapping(target = "bookerId",source = "booker.id")
    BookingShortDto toLastBookingDto(BookingEntity booking);

    @Mapping(target = "start",source = "start",qualifiedByName = "convertToTimestamp")
    @Mapping(target = "end",source = "end",qualifiedByName = "convertToTimestamp")
    BookingEntity toEntity(Booking booking);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "start",source = "start",qualifiedByName = "convertToTimestamp")
    @Mapping(target = "end",source = "end",qualifiedByName = "convertToTimestamp")
    void updateEntity(Booking booking, @MappingTarget BookingEntity entity);

    @Named("convertToTimestamp")
    default Timestamp convertToTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    @Named(value = "convertToLocalDateTime")
    default LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }
}
