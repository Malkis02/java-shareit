package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring",uses = {
        ItemMapper.class,
        UserMapper.class
})
public interface CommentMapper {
    @Mapping(target = "id",source = "id")
    @Mapping(target = "authorName",source = "author.name")
    @Mapping(target = "text",source = "text")
    @Mapping(target = "created",source = "created")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id",source = "id")
    @Mapping(target = "author.name",source = "authorName")
    @Mapping(target = "text",source = "text")
    @Mapping(target = "created",source = "created")
    Comment toComment(CommentDto comment);


}
