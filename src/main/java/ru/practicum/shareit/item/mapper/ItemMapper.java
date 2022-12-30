package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
        BookingRepositoryMapper.class
})
public interface ItemMapper {

    ItemDto mapToItemDto(Item item);

    @Mapping(target = "owner.id", source = "userId")
    Item mapToItem(ItemDto itemDto, Long userId);

    @Mapping(target = "id", ignore = true)
   // @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item mapToItem(UpdateItemDto itemDto, Long userId);

    @Mapping(target = "id",source = "id")
    @Mapping(target = "comments",source = "comments")
    ItemBookingDto toItemBookingDto(ItemEntity item);


    Item toItem(ItemBookingDto itemBookingDto);
}
