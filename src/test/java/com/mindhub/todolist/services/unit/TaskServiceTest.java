package com.mindhub.todolist.services.unit;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityId;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.UserEntityService;
import com.mindhub.todolist.services.impl.TaskServiceImpl;
import com.mindhub.todolist.services.impl.UserEntityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserEntityServiceImpl userEntityService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should be able to create new task")
    void createNewTaskShouldCallsRepositoryAndReturnsExpectedResult()
            throws UserEntityNotFoundException,NoSuchFieldException, IllegalAccessException {

        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        UserEntity mockUser = new UserEntity();

        Field idField = UserEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockUser, 10L);
        mockUser.setEmail(email);

        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(userEntityService.getUserEntityById(mockUser.getId())).thenReturn(mockUser);

        UserEntityId mockUserId = new UserEntityId(mockUser.getId());

        NewTask newTask = new NewTask("Test Title", "Test Description", TaskStatus.PENDING, mockUserId);

        Task savedTask = new Task(newTask.title(), newTask.description(), newTask.status());
        savedTask.setUserEntity(mockUser);

        Field idTaskField = Task.class.getDeclaredField("Id");
        idTaskField.setAccessible(true);
        idTaskField.set(savedTask, 10L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskDTO result = taskService.createNewTask(newTask, authentication);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();

        assertEquals(newTask.title(), capturedTask.getTitle());
        assertEquals(newTask.description(), capturedTask.getDescription());
        assertEquals(newTask.status(), capturedTask.getStatus());
        assertEquals(mockUser, capturedTask.getUserEntity());

        assertNotNull(result);
        assertEquals(savedTask.getTitle(), result.getTitle());
        assertEquals(savedTask.getDescription(), result.getDescription());
        assertEquals(newTask.status(), result.getStatus());
    }
}
