package com.taba.user.dto;

import com.taba.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "joinedAt", source = "createdAt")
    @Mapping(target = "friendCount", ignore = true)
    @Mapping(target = "sentLetters", ignore = true)
    UserDto toDto(User user);
}

