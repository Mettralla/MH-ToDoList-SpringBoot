package com.mindhub.todolist.repositories.integration;

import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserEntityRepositoryTest {

    @TestConfiguration
    static class TestConfig {}

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @BeforeAll
    static void setUp(@Autowired UserEntityRepository userEntityRepository,
                      @Autowired PasswordEncoder passwordEncoder) {
        UserEntity user1 = new UserEntity(
                "testUser",
                passwordEncoder.encode("12345"),
                "test@example.com"
        );
        UserEntity user2 = new UserEntity(
                "preUpdateTestUser",
                passwordEncoder.encode("12345"),
                "testForUpdate@example.com"
        );

        userEntityRepository.save(user1);
        userEntityRepository.save(user2);
    }

    @Test
    @DisplayName("Should create a new user entity and assign an ID")
    void testShouldCreateNewUserEntity() {
        long initialCount = userEntityRepository.count();

        UserEntity user = new UserEntity("pedroSanchez", passwordEncoder.encode("password123"), "pedroSanchez@example.com");
        userEntityRepository.save(user);

        assertNotNull(user.getId());

        long finalCount = userEntityRepository.count();
        assertEquals(initialCount + 1, finalCount);
    }

    @Test
    @DisplayName("Should find user entity by email")
    void testShouldFindUserEntityByEmail() {
        Optional<UserEntity> result = userEntityRepository.findByEmail("test@example.com");

        assertThat(result.isPresent(), is(true));

        result.ifPresent(foundUser -> {
            assertThat(foundUser.getUsername(), is("testUser"));
            assertThat(foundUser.getEmail(), is("test@example.com"));
        });
    }

    @Test
    @DisplayName("Should return empty if user does not exist by email")
    void testUserEntityShouldNotExistByEmail() {
        Optional<UserEntity> result = userEntityRepository.findByEmail("blank@dontexist.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return true if user exists by email")
    void testUserEntityShouldExistsByEmail() {
        boolean exists = userEntityRepository.existsByEmail("test@example.com");
        assertThat(exists, is(true));
    }

    @Test
    @DisplayName("Should return false if user does not exist by email")
    void testUserEntityShouldNotExistsByEmail() {
        boolean notExists = userEntityRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists, is(false));
    }

    @Test
    @DisplayName("Should update an existing user entity")
    void testUpdateUserEntity() {
        Optional<UserEntity> existingUserOptional = userEntityRepository.findByEmail("testForUpdate@example.com");

        assertTrue(existingUserOptional.isPresent(), "User should exist");

        UserEntity existingUser = existingUserOptional.get();

        existingUser.setUsername("updatedTestUser");

        UserEntity updatedUser = userEntityRepository.save(existingUser);

        assertNotNull(updatedUser);
        assertEquals("updatedTestUser", updatedUser.getUsername());
    }

    @Test
    @DisplayName("Should retrieve all user entities")
    void testGetAllUserEntities() {
        long usersInDB = userEntityRepository.count();
        List<UserEntity> users = userEntityRepository.findAll();

        assertNotNull(users);
        assertEquals(usersInDB, users.size());
    }

    @Test
    @DisplayName("Should not save user entity with empty username")
    void testUserEntityShouldNotSaveWithEmptyUsername() {
        UserEntity invalidUser = new UserEntity("", "validPassword", "testValidation@example.com");

        assertThrows(ConstraintViolationException.class, () -> {
            userEntityRepository.save(invalidUser);
        });
    }

    @Test
    @DisplayName("Should not save user entity with empty password")
    void testUserEntityShouldNotSaveWithEmptyPassword() {
        UserEntity invalidUser = new UserEntity("validUser", "", "test@example.com");

        assertThrows(ConstraintViolationException.class, () -> {
            userEntityRepository.save(invalidUser);
        });
    }

    @Test
    @DisplayName("Should not save user entity with empty email")
    void testUserEntityShouldNotSaveWithEmptyEmail() {
        UserEntity invalidUser = new UserEntity("validUser", "validPassword", "");

        assertThrows(ConstraintViolationException.class, () -> {
            userEntityRepository.save(invalidUser);
        });
    }

    @Test
    @DisplayName("Should not save user entity with invalid email format")
    void testUserEntityShouldNotSaveWithInvalidEmailFormat() {
        UserEntity invalidUser = new UserEntity("validUser", "validPassword", "invalidEmailFormat");

        assertThrows(ConstraintViolationException.class, () -> {
            userEntityRepository.save(invalidUser);
        });
    }

    @Test
    @DisplayName("Should not save user entity with null username")
    void testUserEntityShouldNotSaveWithNullUsername() {
        UserEntity invalidUser = new UserEntity(null, "validPassword", "testValidation@example.com");

        assertThrows(ConstraintViolationException.class, () -> {
            userEntityRepository.save(invalidUser);
        });
    }

    @Test
    @DisplayName("Should delete user entity by id")
    void testDeleteUserEntity() {
        UserEntity user = new UserEntity("deleteTestUser", passwordEncoder.encode("password123"), "deleteTestUser@example.com");
        userEntityRepository.save(user);

        Optional<UserEntity> userBeforeDelete = userEntityRepository.findById(user.getId());
        assertTrue(userBeforeDelete.isPresent(), "User should exist before deletion");

        userEntityRepository.delete(user);

        Optional<UserEntity> userAfterDelete = userEntityRepository.findById(user.getId());
        assertFalse(userAfterDelete.isPresent(), "User should be deleted");
    }
}
