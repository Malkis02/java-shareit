package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        CommentRepositoryMapper.class,
        BookingRepositoryMapper.class,
        UserRepositoryMapper.class
})
public interface ItemRepositoryMapper {


    Item toItem(ItemEntity entity);


    ItemEntity toItemEntity(Item item);

    @Mapping(target = "id",ignore = true)
    void updateEntity(Item item, @MappingTarget ItemEntity entity);
}
