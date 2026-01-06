package org.ausiankou.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для ответа с данными пользователя")
public class UserResponseDto extends RepresentationModel<UserResponseDto> {

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30")
    private Integer age;

    @Schema(description = "Дата создания пользователя", example = "2024-01-15T10:30:00")
    private LocalDateTime createAt;
}