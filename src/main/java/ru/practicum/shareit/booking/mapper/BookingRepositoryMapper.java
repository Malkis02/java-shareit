package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingUpdateResponseDto;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserRepositoryMapper.class,
                ItemRepositoryMapper.class,
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingRepositoryMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "booker.id", source = "bookerId")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingEntity mapToEntity(BookingShortDto bookingShortDto);

    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingUpdateResponseDto toBookingUpdateResponseDto(BookingEntity booking);

    @Mapping(target = "item.id", source = "itemId")
    BookingEntity toBookingEntity(BookingCreateRequestDto booking);

    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingDto toBookingDto(BookingEntity booking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingShortDto toLastBookingDto(BookingEntity booking);
}
