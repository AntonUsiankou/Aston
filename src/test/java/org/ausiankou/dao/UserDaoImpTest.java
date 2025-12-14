package org.ausiankou.dao;

import org.ausiankou.exception.UserServiceException;
import org.ausiankou.model.User;
import org.ausiankou.util.HibernateUtilTest;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImpTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("AstonTrainee")
                    .withUsername("AstonTrainee")
                    .withPassword("AstonTrainee");

    private UserDao userDao;

    @BeforeAll
    void setup() {
        HibernateUtilTest.configureHibernate(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        userDao = new UserDaoImpl();
    }

    private String generateUniqueEmail() {
        return "test-" + UUID.randomUUID().toString().replace("-", "") + "@test.com";
    }

    @Test
    @DisplayName("Должен сохранить пользователя")
    void shouldSaveUser() {
        // Given
        String uniqueEmail = generateUniqueEmail();
        String uniqueName = "TestUser_" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(uniqueName, uniqueEmail, 30);

        // When
        User savedUser = userDao.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(uniqueName);
        assertThat(savedUser.getEmail()).isEqualTo(uniqueEmail);
        assertThat(savedUser.getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("Должен найти пользователя по ID")
    void shouldFindUserById() {
        // Given
        List<User> existingUsers = userDao.findAll();
        if (!existingUsers.isEmpty()) {
            User existingUser = existingUsers.get(0);

            // When
            Optional<User> foundUser = userDao.findById(existingUser.getId());

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getId()).isEqualTo(existingUser.getId());
            assertThat(foundUser.get().getName()).isEqualTo(existingUser.getName());
        } else {
            String uniqueEmail = generateUniqueEmail();
            User user = new User("Test User", uniqueEmail, 25);
            User savedUser = userDao.save(user);

            Optional<User> foundUser = userDao.findById(savedUser.getId());
            assertThat(foundUser).isPresent();
        }
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующего ID")
    void shouldReturnEmptyOptionalForNonExistentId() {
        Optional<User> foundUser = userDao.findById(999999L);
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Должен найти всех пользователей")
    void shouldFindAllUsers() {
        // Given
        User user1 = new User("FindAll_User1", generateUniqueEmail(), 20);
        User user2 = new User("FindAll_User2", generateUniqueEmail(), 25);
        User user3 = new User("FindAll_User3", generateUniqueEmail(), 30);

        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);

        // When
        List<User> users = userDao.findAll();

        // Then
        assertThat(users)
                .extracting(User::getName)
                .contains(user1.getName(), user2.getName(), user3.getName());
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void shouldUpdateUser() {
        // Given
        String oldEmail = generateUniqueEmail();
        User user = new User("OldName_Update", oldEmail, 20);
        User savedUser = userDao.save(user);

        // When
        String newEmail = generateUniqueEmail();
        savedUser.setName("NewName_Update");
        savedUser.setEmail(newEmail);
        savedUser.setAge(30);
        User updatedUser = userDao.update(savedUser);

        // Then
        assertThat(updatedUser.getName()).isEqualTo("NewName_Update");
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        assertThat(updatedUser.getAge()).isEqualTo(30);

        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("NewName_Update");
        assertThat(foundUser.get().getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    void shouldDeleteUser() {
        // Given
        String uniqueEmail = generateUniqueEmail();
        User user = new User("ToDelete_User", uniqueEmail, 40);
        User savedUser = userDao.save(user);

        // When
        userDao.delete(savedUser.getId());

        // Then
        Optional<User> foundUser = userDao.findById(savedUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Должен найти пользователя по email")
    void shouldFindUserByEmail() {
        // Given
        String uniqueEmail = generateUniqueEmail();
        User user = new User("EmailTest_User", uniqueEmail, 35);
        userDao.save(user);

        // When
        Optional<User> foundUser = userDao.findByEmail(uniqueEmail);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(uniqueEmail);
    }

    @Test
    @DisplayName("Должен найти пользователей по имени")
    void shouldFindUsersByName() {
        // Given
        String prefix = "NameSearch_" + UUID.randomUUID().toString().substring(0, 6);
        String email1 = generateUniqueEmail();
        String email2 = generateUniqueEmail();
        String email3 = generateUniqueEmail();

        userDao.save(new User(prefix + "_John", email1, 30));
        userDao.save(new User(prefix + "_John", email2, 25));
        userDao.save(new User(prefix + "_Jane", email3, 28));

        // When
        List<User> foundUsers = userDao.findByName(prefix + "_John");

        // Then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers)
                .extracting(User::getName)
                .allMatch(name -> name.contains(prefix + "_John"));
    }

    @Test
    @DisplayName("Должен выбросить исключение при дублировании email")
    void shouldThrowExceptionForDuplicateEmail() {
        // Given
        String duplicateEmail = generateUniqueEmail();
        User user1 = new User("Duplicate1_User", duplicateEmail, 20);
        userDao.save(user1);

        User user2 = new User("Duplicate2_User", duplicateEmail, 25);

        // When and then
        assertThatThrownBy(() -> userDao.save(user2))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении на существующий email")
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        // Given
        String email1 = generateUniqueEmail();
        String email2 = generateUniqueEmail();

        User user1 = new User("UpdateDuplicate1", email1, 20);
        User user2 = new User("UpdateDuplicate2", email2, 25);

        User savedUser1 = userDao.save(user1);
        userDao.save(user2);

        savedUser1.setEmail(email2);

        // When and Then
        assertThatThrownBy(() -> userDao.update(savedUser1))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Email already exists for another user");
    }

    @AfterAll
    void tearDown() {
        HibernateUtilTest.shutdown();
    }
}