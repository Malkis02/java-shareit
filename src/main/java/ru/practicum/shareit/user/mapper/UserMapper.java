package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper mapper = Mappers.getMapper(UserMapper.class);

    User mapToUser(UserDto userDto);

    UserDto mapToUserDto(User user);
}
