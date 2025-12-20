package org.ausiankou.service;

import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequest);
    UserResponseDto getUserById(Long id);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto updateUser(Long id, UserRequestDto userRequest);
    void deleteUser(Long id);
}