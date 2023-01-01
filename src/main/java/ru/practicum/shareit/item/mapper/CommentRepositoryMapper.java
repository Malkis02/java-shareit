package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;

@Mapper(componentModel = "spring",uses = {
        UserRepositoryMapper.class,
        ItemRepositoryMapper.class
})
public interface CommentRepositoryMapper {

    @Mapping(target = "author",source = "author")
    @Mapping(target = "author.name",source = "author.name")
    @Mapping(target = "item",source = "item")
    @Mapping(target = "item.id",source = "item.id")
    @Mapping(target = "text",source = "text")
    @Mapping(target = "id",source = "id")
    Comment toComment(CommentEntity entity);

    @Mapping(target = "author",source = "author")
    @Mapping(target = "author.name",source = "author.name")
    @Mapping(target = "item",source = "item")
    @Mapping(target = "item.id",source = "item.id")
    @Mapping(target = "text",source = "text")
    @Mapping(target = "id",source = "id")
    CommentEntity toEntity(Comment comment);

}
