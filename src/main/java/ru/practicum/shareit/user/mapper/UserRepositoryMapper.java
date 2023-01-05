package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.UserEntity;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRepositoryMapper {
    UserEntity mapToUserEntity(UserDto userDto);

    UserDto toUserDto(UserEntity user);

    UserEntity mapToUserEntity(UpdateUserDto userDto);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "name",source = "name")
    void updateEntity(UserEntity user, @MappingTarget UserEntity entity);
}
