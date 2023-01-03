package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;


@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        CommentMapper.class,
        BookingRepositoryMapper.class,
        CommentRepositoryMapper.class
})
public interface ItemMapper {
    ItemDto mapToItemDto(Item item);

    @Mapping(target = "owner.id", source = "userId")
    Item mapToItem(ItemDto itemDto, Long userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item mapToItem(UpdateItemDto itemDto, Long userId);

    ItemBookingDto toItemBookingDto(ItemEntity item);

    ItemBookingDto toItemBookingDto(Item item);


}
