package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRepositoryMapper {
    @Mapping(target = "owner.id", source = "userId")
    @Mapping(target = "request.id",source = "itemDto.requestId")
    ItemEntity mapToItem(ItemDto itemDto, Long userId);

    @Mapping(target = "requestId",source = "item.request.id")
    ItemDto mapToItemDto(ItemEntity item);

    @Mapping(target = "lastBooking.bookerId",source = "lastBooking.booker.id")
    @Mapping(target = "nextBooking.bookerId",source = "nextBooking.booker.id")
    @Mapping(target = "comments",source = "comments")
    ItemBookingDto toItemBookingDto(ItemEntity item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    ItemEntity mapToItem(UpdateItemDto itemDto, Long userId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "text", source = "text")
    CommentDto toCommentDto(CommentEntity comment);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ItemEntity item, @MappingTarget ItemEntity entity);
}
