package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        UserRepositoryMapper.class,
        CommentMapper.class
})
public interface ItemRepositoryMapper {
    @Mapping(target = "owner.id", source = "userId")
    ItemEntity mapToItem(ItemDto itemDto, Long userId);

    ItemDto mapToItemDto(ItemEntity item);

    ItemBookingDto toItemBookingDto(ItemEntity item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    ItemEntity mapToItem(UpdateItemDto itemDto, Long userId);

    @Mapping(target = "id",ignore = true)
    void updateEntity(ItemEntity item, @MappingTarget ItemEntity entity);
}
