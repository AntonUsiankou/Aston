package org.ausiankou.service;

import org.ausiankou.dto.UserMapper;
import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.ausiankou.model.User;
import org.ausiankou.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Проверка создания пользователя")
    void checkCreateUser() {
        // Given
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("John").email("john@test.com").age(25).build();

        User user = User.builder()
                .name("John").email("john@test.com").age(25).build();

        User savedUser = User.builder()
                .id(1L).name("John").email("john@test.com").age(25)
                .createdAt(LocalDateTime.now()).build();

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1L).name("John").email("john@test.com").age(25)
                .createAt(savedUser.getCreatedAt()).build();

        given(userRepository.existsByEmail("john@test.com")).willReturn(false);
        given(userMapper.toEntity(any(UserRequestDto.class))).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toResponseDto(savedUser)).willReturn(responseDto);

        // When
        UserResponseDto result = userService.createUser(requestDto);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Проверка возвращения пользователя по ID")
    void checkGetUserById() {
        // Given
        User user = User.builder().id(1L).name("John").build();
        UserResponseDto responseDto = new UserResponseDto(1L, "John", "john@test.com", 25, null);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userMapper.toResponseDto(user)).willReturn(responseDto);

        // When
        UserResponseDto result = userService.getUserById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Проверка не существующего юзера и выбрасывание ошибки")
    void checkUnexistUser() {
        // Given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(org.ausiankou.exception.UserServiceException.class)  // Ваше исключение
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Возвращение страницы с пользователями")
    void getPageOfAllUsers() {
        // Given
        User user1 = User.builder().id(1L).name("John").build();
        User user2 = User.builder().id(2L).name("Jane").build();
        Page<User> page = new PageImpl<>(Arrays.asList(user1, user2));
        Pageable pageable = PageRequest.of(0, 10);

        UserResponseDto dto1 = new UserResponseDto(1L, "John", "john@test.com", 25, null);
        UserResponseDto dto2 = new UserResponseDto(2L, "Jane", "jane@test.com", 30, null);

        given(userRepository.findAll(pageable)).willReturn(page);
        given(userMapper.toResponseDto(user1)).willReturn(dto1);
        given(userMapper.toResponseDto(user2)).willReturn(dto2);

        // When
        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Jane");
    }

    @Test
    @DisplayName("Обновление пользователя и возврат его")
    void updateUser() {
        // Given
        UserRequestDto requestDto = new UserRequestDto("John Updated", "john.updated@test.com", 26);
        User existingUser = User.builder().id(1L).name("John").email("john@test.com").age(25).build();
        User updatedUser = User.builder().id(1L).name("John Updated").email("john.updated@test.com").age(26).build();
        UserResponseDto responseDto = new UserResponseDto(1L, "John Updated", "john.updated@test.com", 26, null);

        given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByEmail("john.updated@test.com")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(updatedUser);
        given(userMapper.toResponseDto(updatedUser)).willReturn(responseDto);

        // When
        UserResponseDto result = userService.updateUser(1L, requestDto);

        // Then
        assertThat(result.getName()).isEqualTo("John Updated");
        verify(userRepository).save(existingUser);
    }

    @Test @DisplayName("Проверка удаления пользователя")
    void checkDeleteUser() {
        // Given
        User user = User.builder().id(1L).email("a@b.com").name("A").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        // When
        userService.deleteUser(1L); // Then
        verify(userRepository).deleteById(1L); }
}