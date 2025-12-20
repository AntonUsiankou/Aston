package org.ausiankou.dto;

import org.ausiankou.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(UserRequestDto dto){
        if(dto == null){
            return null;
        }

        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .email(String.valueOf(dto.getAge()))
                .build();
    }
}
