package com.mindhub.todolist.repositories.integration;

import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserEntityRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @BeforeAll
    static void setUp(@Autowired UserEntityRepository userEntityRepository,
                      @Autowired TaskRepository taskRepository,
                      @Autowired PasswordEncoder passwordEncoder) {
        UserEntity user1 = new UserEntity(
                "testUser",
                passwordEncoder.encode("12345"),
                "test@example.com"
        );

        user1.setRole(RoleType.USER);

        UserEntity user2 = new UserEntity(
                "preUpdateTestUser",
                passwordEncoder.encode("12345"),
                "testForUpdate@example.com"
        );

        user2.setRole(RoleType.USER);

        userEntityRepository.save(user1);
        userEntityRepository.save(user2);
    }

    @Test
    @DisplayName("Should save and retrieve task correctly")
    void testShouldCreateNewTaskAndRetrieveIt() {
        Optional<UserEntity> testUser = userEntityRepository.findByEmail("test@example.com");
        assertTrue(testUser.isPresent(), "User should exist");

        UserEntity existingTestUser = testUser.get();

        Task task = new Task("Task 1", "Description of Task 1", TaskStatus.PENDING);

        existingTestUser.addTask(task);
        taskRepository.save(task);

        Optional<Task> retrievedTask = taskRepository.findById(task.getId());

        assertTrue(retrievedTask.isPresent());
        assertEquals("Task 1", retrievedTask.get().getTitle());
        assertEquals("Description of Task 1", retrievedTask.get().getDescription());
        assertEquals(retrievedTask.get().getUserEntity(), existingTestUser, "Task should be associated with the user");
        assertTrue(existingTestUser.getTasks().contains(retrievedTask.get()), "User should have the task in their task list");
    }

    @Test
    @DisplayName("Should update a task successfully")
    void testUpdateTask() {
        Optional<UserEntity> testUser = userEntityRepository.findByEmail("testForUpdate@example.com");
        assertTrue(testUser.isPresent(), "User should exist");

        UserEntity existingTestUser = testUser.get();

        Task task = new Task("Task 2", "Description of Task 2", TaskStatus.PENDING);

        existingTestUser.addTask(task);
        taskRepository.save(task);

        task.setTitle("Updated Task 2");
        task.setDescription("Updated Description of Task 2");
        taskRepository.save(task);

        Optional<Task> updatedTask = taskRepository.findById(task.getId());

        assertTrue(updatedTask.isPresent());
        assertEquals("Updated Task 2", updatedTask.get().getTitle());
        assertEquals("Updated Description of Task 2", updatedTask.get().getDescription());
    }

    @Test
    @DisplayName("Should delete a task successfully")
    void testDeleteTask() {
        long initialCount = taskRepository.count();
        Task task = new Task("Task 3", "Description of Task 3", TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        taskRepository.deleteById(task.getId());

        Optional<Task> deletedTask = taskRepository.findById(task.getId());
        assertFalse(deletedTask.isPresent());
        long finalCount = taskRepository.count();
        assertEquals(initialCount, finalCount);
    }

    @Test
    @DisplayName("Should retrieve all task entities")
    void testGetAllTaskEntities() {
        long tasksInDB = taskRepository.count();
        List<Task> tasks = taskRepository.findAll();

        assertNotNull(tasks);
        assertEquals(tasksInDB, tasks.size());
    }

    @Test
    @DisplayName("Should check if task exists")
    void testTaskExists() {
        Task task = new Task("Task 4", "Description of Task 4", TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        boolean exists = taskRepository.existsById(task.getId());
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should not save task entity with empty title")
    void testTaskEntityShouldNotSaveWithEmptyTitle() {
        Task invalidTask = new Task("", "Valid description", TaskStatus.PENDING);

        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(invalidTask);
        });
    }

    @Test
    @DisplayName("Should not save task entity with empty description")
    void testTaskEntityShouldNotSaveWithEmptyDescription() {
        Task invalidTask = new Task("Valid title", "", TaskStatus.PENDING);

        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(invalidTask);
        });
    }

    @Test
    @DisplayName("Should not save task entity with null title")
    void testTaskEntityShouldNotSaveWithNullTitle() {
        Task invalidTask = new Task(null, "Valid description", TaskStatus.PENDING);

        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(invalidTask);
        });
    }

    @Test
    @DisplayName("Should not save task entity with null description")
    void testTaskEntityShouldNotSaveWithNullDescription() {
        Task invalidTask = new Task("Valid title", null, TaskStatus.PENDING);

        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(invalidTask);
        });
    }

    @Test
    @DisplayName("Should not save task entity with null status")
    void testTaskEntityShouldNotSaveWithNullStatus() {
        Task invalidTask = new Task("Valid title", "Valid description", null);

        assertThrows(ConstraintViolationException.class, () -> {
            taskRepository.save(invalidTask);
        });
    }
}
