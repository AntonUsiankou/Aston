package org.ausiankou.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.ausiankou.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Функция создания пользователя должна возвращать созданного пользователя.")
    void createUser_ReturnCreatedUser() throws Exception {
        // Given
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("John")
                .email("john@test.com")
                .age(25)
                .build();

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .age(25)
                .createAt(LocalDateTime.now())
                .build();

        given(userService.createUser(any(UserRequestDto.class))).willReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@test.com")));
    }

    @Test
    @DisplayName("Метод createUser с неверными данными должен возвращать ошибку \"Bad Request\".")
    void userWithInvalidData() throws Exception {
        // Given
        String invalidJson = """
            {
                "name": "",
                "email": "invalid-email",
                "age": -5
            }
            """;

        // When Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Функция getUserById должна возвращать пользователя.")
    void ShouldReturnUser() throws Exception {
        // Given
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .age(25)
                .createAt(LocalDateTime.now())
                .build();

        given(userService.getUserById(1L)).willReturn(responseDto);

        // When Then
        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")));
    }

    @Test
    @DisplayName("Метод getUserById при ошибке возвращает NotFound.")
    void whenUserNotFound() throws Exception {
        // Given
        given(userService.getUserById(999L))
                .willThrow(new jakarta.persistence.EntityNotFoundException("User not found"));

        // When Then
        mockMvc.perform(get("/api/v1/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Возвращает страницу пользователей")
    void returnUsersPage() throws Exception {
        // Given
        UserResponseDto user1 = UserResponseDto.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .age(25)
                .build();

        UserResponseDto user2 = UserResponseDto.builder()
                .id(2L)
                .name("Jane")
                .email("jane@test.com")
                .age(30)
                .build();

        List<UserResponseDto> users = Arrays.asList(user1, user2);

        given(userService.getAllUsers()).willReturn(users);

        // When Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("John")))
                .andExpect(jsonPath("$.content[1].name", is("Jane")));
    }

    @Test
    @DisplayName("обновить пользователя, который должен вернуть обновленного пользователя")
    void returnUpdatedUser() throws Exception{
        // Given
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("John Updated")
                .email("john.updated@test.com")
                .age(31)
                .build();

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@test.com")
                .age(31)
                .build();

        given(userService.updateUser(eq(1L), any(UserRequestDto.class))).willReturn(responseDto);//When Then

        // When Then
        mockMvc.perform(put("/api/v1/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated")));
    }
    @Test
    @DisplayName("Метод updateUser с неправильными данными")
    void withInvalidData() throws Exception {
        // Given
        String invalidJson = """
            {
                "name": "",
                "email": "invalid",
                "age": -1
            }
            """;

        // When Then
        mockMvc.perform(put("/api/v1/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("удаление несуществующего пользователя")
    void deleteUserNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When Then
        mockMvc.perform(delete("/api/v1/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("При нулевом возрасте - BAD REQUEST")
    void userWithNullAge() throws Exception {
        // Given
        String invalidJson = """
            {
                "name": "John",
                "email": "john@test.com",
                "age": null
            }
            """;

        // When Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("C пустым именем")
    void userWithEmptyName() throws Exception {
        // Given
        String invalidJson = """
            {
                "name": "",
                "email": "john@test.com",
                "age": 25
            }
            """;

        // When Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
