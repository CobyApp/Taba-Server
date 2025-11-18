package com.taba.user.dto;

import com.taba.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    default UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .joinedAt(user.getCreatedAt())
                .friendCount(0) // UserService에서 계산하여 설정
                .sentLetters(0) // UserService에서 계산하여 설정
                .build();
    }
}

