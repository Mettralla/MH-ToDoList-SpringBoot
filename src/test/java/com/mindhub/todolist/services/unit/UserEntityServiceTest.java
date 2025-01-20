package com.mindhub.todolist.services.unit;

import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.impl.UserEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private UserEntityServiceImpl userEntityService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockUser = new UserEntity("testUser", "password123", "test@domain.com");

        Field idField = UserEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockUser, 1L);
    }

    @Test
    void testGetUserEntityByIdShouldBeSuccess() throws UserEntityNotFoundException {
        when(userEntityRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));

        UserEntity userEntity = userEntityService.getUserEntityById(1L);

        verify(userEntityRepository, times(1)).findById(1L);

        assertNotNull(userEntity);
        assertEquals(1L, userEntity.getId());
        assertEquals("testUser", userEntity.getUsername());
        assertEquals("test@domain.com", userEntity.getEmail());
    }

    @Test
    void testGetUserEntityByIdShouldGetUserNotFound() {
        when(userEntityRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(UserEntityNotFoundException.class, () -> {
            userEntityService.getUserEntityById(1L);
        });

        verify(userEntityRepository, times(1)).findById(1L);
    }
}
