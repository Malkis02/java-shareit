package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface CommentRepositoryMapper {
    @Mapping(target = "author",source = "user")
    @Mapping(target = "item.id",source = "itemId")
    @Mapping(target = "text",source = "entity.text")
    @Mapping(target = "id",source = "entity.id")
    Comment toComment(CommentEntity entity, UserEntity user, Long itemId);

    @Mapping(target = "author",source = "user")
    @Mapping(target = "item.id",source = "itemId")
    @Mapping(target = "text",source = "comment.text")
    @Mapping(target = "id",source = "comment.id")
    CommentEntity toEntity(Comment comment,UserEntity user, Long itemId);
}
