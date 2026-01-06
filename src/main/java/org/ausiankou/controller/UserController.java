package org.ausiankou.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ausiankou.dto.UserRequestDto;
import org.ausiankou.dto.UserResponseDto;
import org.ausiankou.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с указанными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto userRequest) {
        UserResponseDto createdUser = userService.createUser(userRequest);
        EntityModel<UserResponseDto> entityModel = userModelAssembler.toModel(createdUser);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                             .body(entityModel);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному идентификатору"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        Optional<UserResponseDto> userOpt = Optional.ofNullable(userService.getUserById(id));
        return userOpt .map(user -> ResponseEntity.ok(userModelAssembler.toModel(user))) .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список пользователей с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список пользователей")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();

        List<EntityModel<UserResponseDto>> userModels = users.stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(userModels));
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequest) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(userModelAssembler.toModel(updatedUser));
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "409", description = "Конфликт данных")
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
