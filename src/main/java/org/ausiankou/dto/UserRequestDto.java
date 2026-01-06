package org.ausiankou.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для запроса создания/обновления пользователя")
public class UserRequestDto {

    @NotBlank(message = "Name is required")
    @Schema(description = "Имя пользователя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    @Schema(description = "Возраст пользователя", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;
}