package org.ausiankou.service;

import org.ausiankou.dto.UserMapper;
import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.ausiankou.dto.events.UserCreatedEvent;
import org.ausiankou.dto.events.UserDeletedEvent;
import org.ausiankou.model.User;
import org.ausiankou.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("Проверка создания пользователя")
    void checkCreateUser() {
        // Given
        UserRequestDto request = new UserRequestDto("Sam", "sam@example.com", 25);
        User userEntity = User.builder()
                .id(1L)
                .name("Sam")
                .email("sam@example.com")
                .age(25)
                .build();
        UserResponseDto responseDto = new UserResponseDto(1L, "Sam", "sam@example.com", 25, null);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.createUser(request);

        // Then
        assertEquals("Sam", result.getName());
        verify(userRepository).save(userEntity);
        verify(kafkaProducerService).sendUserCreatedEvent(1L, "sam@example.com", "Sam");
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
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
    @DisplayName("Возвращение списка пользователей")
    void getAllUsersList() {
        // Given
        User user1 = User.builder().id(1L).name("John").build();
        User user2 = User.builder().id(2L).name("Jane").build();

        UserResponseDto dto1 = new UserResponseDto(1L, "John", "john@test.com", 25, null);
        UserResponseDto dto2 = new UserResponseDto(2L, "Jane", "jane@test.com", 30, null);

        given(userRepository.findAll()).willReturn(Arrays.asList(user1, user2));
        given(userMapper.toResponseDto(user1)).willReturn(dto1);
        given(userMapper.toResponseDto(user2)).willReturn(dto2);

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John");
        assertThat(result.get(1).getName()).isEqualTo("Jane");
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
        User userEntity = User.builder()
                .id(1L)
                .name("Sam")
                .email("sam@example.com")
                .age(25)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
        verify(kafkaProducerService).sendUserDeletedEvent(1L, "sam@example.com", "Sam");
        verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
    }
}