package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

@Mapper(componentModel = "spring", uses = {
        ItemRepositoryMapper.class,
        UserRepositoryMapper.class
})
public interface ItemRequestMapper {


    @Mapping(target = "requestor.id",source = "userId")
    @Mapping(target = "created",source = "itemDto.created")
//    @Mapping(target = "items",source = "itemDto.items")
    ItemRequestEntity toItemRequestEntity(ItemRequestDto itemDto,Long userId);

    @Mapping(target = "created",source = "created")
    //@Mapping(target = "items",source = "items")
    ItemRequestDto toItemRequestDto(ItemRequestEntity entity);
}
