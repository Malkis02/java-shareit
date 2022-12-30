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
    Comment toComment(CommentEntity entity);

    @Mapping(target = "author",source = "author")
    CommentEntity toEntity(Comment comment);

}
