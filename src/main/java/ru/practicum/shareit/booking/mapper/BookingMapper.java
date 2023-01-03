package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;


@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        ItemMapper.class
})
public interface BookingMapper {
    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    BookingUpdateResponseDto toBookingUpdateResponseDto(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    Booking toBooking(BookingCreateRequestDto booking);
}
