package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserRepositoryMapper {

    @Mapping(target = "name",source = "name")
    User toUser(UserEntity entity);

    UserEntity toEntity(User user);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "name",source = "name")
    void updateEntity(User user, @MappingTarget UserEntity entity);
}
