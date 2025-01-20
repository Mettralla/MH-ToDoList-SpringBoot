package com.mindhub.todolist.services.integration;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.impl.UserEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class UserEntityServiceTest {
    @Autowired
    private UserEntityServiceImpl userEntityService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll();
        UserEntity testUser = new UserEntity("testuser", "securePassword123", "test@example.com");
        testUser.setRole(RoleType.ADMIN);
        userEntityRepository.save(testUser);
    }

    @Test
    void testShouldCreateNewAdminSuccessfully() throws EmailAlreadyExistsException {
        NewUserEntity newUserTest = new NewUserEntity("newUser", "password123", "newuser@example.com");

        UserEntityDTO createdAdmin = userEntityService.createNewAdmin(newUserTest, authentication);
        assertThat(createdAdmin).isNotNull();
        assertThat(createdAdmin.getUsername()).isEqualTo("newUser");
        assertThat(userEntityRepository.findByEmail("newuser@example.com")).isPresent();
    }

    @Test
    void testCreateNewAdminShouldThrowsEmailAlreadyExistsExceptionWhenEmailExists() {
        NewUserEntity duplicatedUser = new NewUserEntity("duplicated", "password123", "test@example.com");

        assertThatThrownBy(() -> userEntityService.createNewAdmin(duplicatedUser, authentication))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists: test@example.com");
    }

    @Test
    void testGetUserByIdShouldReturnsUserSuccessfully() throws EmailAlreadyExistsException {
        NewUserEntity newUserTest = new NewUserEntity("newUser", "password123", "newuser@example.com");
        UserEntityDTO createdUser = userEntityService.createNewAdmin(newUserTest, authentication);

        UserEntity user = userEntityRepository.findById(createdUser.getId()).orElse(null);

        assertNotNull(user, "User should exist in the repository");
        assertEquals(createdUser.getEmail(), user.getEmail(), "The email should match");
    }
}
