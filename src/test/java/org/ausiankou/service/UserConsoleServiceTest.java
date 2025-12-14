package org.ausiankou.service;

import org.ausiankou.dao.UserDao;
import org.ausiankou.exception.UserServiceException;
import org.ausiankou.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class UserConsoleServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private UserConsoleService consoleService;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("Test User", "test@example.com", 30);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());

        consoleService = new UserConsoleService();

        Field userDaoField = UserConsoleService.class.getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(consoleService, userDao);

        Field scannerField = UserConsoleService.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(consoleService, scanner);
    }

    @Test
    @DisplayName("Должен создать пользователя")
    @Timeout(2)
    void shouldCreateUser() {
        // Given
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("John Doe")
                .thenReturn("")
                .thenReturn("john@test.com")
                .thenReturn("");

        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(25);

        when(userDao.findByEmail("john@test.com")).thenReturn(Optional.empty());

        User savedUser = new User("John Doe", "john@test.com", 25);
        savedUser.setId(1L);
        when(userDao.save(any(User.class))).thenReturn(savedUser);

        // When
        consoleService.createUser();

        // Then
        verify(userDao).findByEmail("john@test.com");
        verify(userDao).save(argThat(user ->
                user.getName().equals("John Doe") &&
                        user.getEmail().equals("john@test.com") &&
                        user.getAge() == 25
        ));
    }

    @Test
    @DisplayName("Не должен создавать пользователя с существующим email")
    @Timeout(2)
    void shouldNotCreateUserWithExistingEmail() {
        // Given
        when(scanner.nextLine())
                .thenReturn("")                      // очистка буфера перед именем
                .thenReturn("John Doe")              // имя
                .thenReturn("")                      // очистка буфера перед email
                .thenReturn("existing@example.com"); // существующий email

        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(25);

        when(userDao.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        consoleService.createUser();

        // Then
        verify(userDao).findByEmail("existing@example.com");
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Должен обработать исключение при создании пользователя")
    @Timeout(2)
    void shouldHandleExceptionWhenCreatingUser() {
        // Given
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("John Doe")
                .thenReturn("")
                .thenReturn("john@test.com");

        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(25);

        when(userDao.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(userDao.save(any(User.class)))
                .thenThrow(new UserServiceException("Database error"));

        // When and Then
        Assertions.assertDoesNotThrow(() -> consoleService.createUser());

        verify(userDao).findByEmail("john@test.com");
        verify(userDao).save(any(User.class));
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    @Timeout(2)
    void shouldUpdateUser() {
        // Given
        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(1);

        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("Updated Name")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("")
                .thenReturn("35");

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        User updatedUser = new User("Updated Name", "test@example.com", 35);
        updatedUser.setId(1L);
        when(userDao.update(any(User.class))).thenReturn(updatedUser);

        // When
        consoleService.updateUser();

        // Then
        verify(userDao).findById(1L);
        verify(userDao).update(argThat(user ->
                user.getName().equals("Updated Name") &&
                        user.getAge() == 35
        ));
    }

    @Test
    @DisplayName("Не должен обновлять email на существующий")
    @Timeout(2)
    void shouldNotUpdateToExistingEmail() {
        // Given
        User anotherUser = new User("Another", "another@example.com", 40);
        anotherUser.setId(2L);

        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(1);

        when(scanner.nextLine())
                .thenReturn("")                     // очистка буфера после nextInt()
                .thenReturn("")                     // имя (пропускаем, пустая строка)
                .thenReturn("")                     // очистка буфера перед email
                .thenReturn("another@example.com")  // существующий email
                .thenReturn("");                    // возраст (пропускаем, пустая строка)

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDao.findByEmail("another@example.com"))
                .thenReturn(Optional.of(anotherUser));

        // When
        consoleService.updateUser();

        // Then
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Должен найти пользователя по ID")
    @Timeout(2)
    void shouldFindUserById() {
        // Given
        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(1);
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        consoleService.findUserById();

        // Then
        verify(scanner).nextInt();
        verify(userDao).findById(1L);
    }

    @Test
    @DisplayName("Должен вывести сообщение при ненайденном пользователе")
    @Timeout(2)
    void shouldShowMessageWhenUserNotFound() {
        // Given
        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(999);
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        // When
        consoleService.findUserById();

        // Then
        verify(userDao).findById(999L);
    }

    @Test
    @DisplayName("Должен найти всех пользователей")
    @Timeout(2)
    void shouldFindAllUsers() {
        // Given
        List<User> users = List.of(
                new User("User1", "user1@example.com", 20),
                new User("User2", "user2@example.com", 25)
        );
        when(userDao.findAll()).thenReturn(users);

        // When
        consoleService.findAllUsers();

        // Then
        verify(userDao).findAll();
    }

    @Test
    @DisplayName("Должен удалить пользователя с подтверждением")
    @Timeout(2)
    void shouldDeleteUserWithConfirmation() {
        // Given
        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(1);

        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("yes");

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        consoleService.deleteUser();

        // Then
        verify(userDao).findById(1L);
        verify(userDao).delete(1L);
    }

    @Test
    @DisplayName("Не должен удалять пользователя без подтверждения")
    @Timeout(2)
    void shouldNotDeleteUserWithoutConfirmation() {
        // Given
        when(scanner.hasNextInt()).thenReturn(true);
        when(scanner.nextInt()).thenReturn(1);

        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("no");

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        consoleService.deleteUser();

        // Then
        verify(userDao).findById(1L);
        verify(userDao, never()).delete(anyLong());
    }

    @Test
    @DisplayName("Должен найти пользователя по email")
    @Timeout(2)
    void shouldFindUserByEmail() {
        // Given
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("test@example.com");

        when(userDao.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        consoleService.findUserByEmail();

        // Then
        verify(userDao).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Должен найти пользователей по имени")
    @Timeout(2)
    void shouldFindUsersByName() {
        // Given
        List<User> users = List.of(testUser);

        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn("Test");

        when(userDao.findByName("Test")).thenReturn(users);

        // When
        consoleService.findUsersByName();

        // Then
        verify(userDao).findByName("Test");
    }

    @AfterEach
    void tearDown() {
        reset(scanner, userDao);
    }
}