package org.ausiankou.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.dto.UserMapper;
import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.ausiankou.exception.UserServiceException;
import org.ausiankou.model.User;
import org.ausiankou.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequest) {
        log.info("Creating user: {}", userRequest.getEmail());

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(userRequest);
        User savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found with ID: " + id));

        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("Getting all users");

        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequest) {
        log.info("Updating user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found"));

        if (!user.getEmail().equals(userRequest.getEmail()) &&
                userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setAge(userRequest.getAge());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new UserServiceException("User not found");
        }

        userRepository.deleteById(id);
    }
}