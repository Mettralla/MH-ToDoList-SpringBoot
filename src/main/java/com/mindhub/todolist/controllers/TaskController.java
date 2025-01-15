package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ================ CURRENT USER ======================

    @GetMapping("/current-user/{id}")
    @Tag(name = "Tasks - Current User", description = "Task Operations related to Current User")
    @Operation(summary = "Get Current User Task By ID", description = "Retrieve a Task entity by its ID.")
    public ResponseEntity<?> getCurrentUserTaskById(@PathVariable Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        try {
            TaskDTO task = taskService.getCurrentUserTaskDTOById(id, authentication);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (UserEntityNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/current-user")
    @Tag(name = "Tasks - Current User", description = "Task Operations related to Current User")
    @Operation(summary = "Get Current User Tasks", description = "Retrieve all Current User Task entities.")
    public ResponseEntity<Object> getCurrentUserTasks(Authentication authentication) throws UserEntityNotFoundException {
        try {
            List<TaskDTO> tasks = taskService.currentUserTasks(authentication);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/current-user")
    @Tag(name = "Tasks - Current User", description = "Task Operations related to Current User")
    @Operation(summary = "Create Current User Task", description = "Create a new Task entity related to Current User.")
    public ResponseEntity<TaskDTO> currentUserCreateTask(@Valid @RequestBody NewTask data, Authentication authentication)  {
        try {
            TaskDTO task = taskService.currentUserCreateNewTask(data, authentication);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/current-user/{id}")
    @Tag(name = "Tasks - Current User", description = "Task Operations related to Current User")
    @Operation(summary = "Delete Current User Tasks", description = "Delete Current User Task entity by its ID.")
    public ResponseEntity<String> currentUserDeleteTask(@PathVariable Long id, Authentication authentication) {
        try {
            taskService.currentUserDeleteTask(id, authentication);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.NO_CONTENT);
        } catch (TaskNotFoundException | UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/current-user/{id}")
    @Tag(name = "Tasks - Current User", description = "Task Operations related to Current User")
    @Operation(summary = "Update Current User Tasks", description = "Update Current User Task entity.")
    public ResponseEntity<Object> currentUserUpdateTask(
            @PathVariable Long id,
            @Valid @RequestBody NewTask updatedData,
            Authentication authentication) {
        try {
            TaskDTO task = taskService.currentUserUpdateTask(id, updatedData, authentication);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotFoundException | UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    // ================ ADMIN PRIVILEGES ======================

    @GetMapping("/{id}")
    @Tag(name = "Tasks - Admin Privileges", description = "Task Operations related to Admin Privileges")
    @Operation(summary = "Get Any Task By ID", description = "Retrieve a Task entity by its ID.")
    public ResponseEntity<?> getTaskById(@PathVariable Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        try {
            TaskDTO task = taskService.getTaskDTOById(id, authentication);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (UserEntityNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping()
    @Tag(name = "Tasks - Admin Privileges", description = "Task Operations related to Admin Privileges")
    @Operation(summary = "Get All Tasks", description = "Retrieve all Task entities.")
    public ResponseEntity<Object> getAllTasks(Authentication authentication) throws UserEntityNotFoundException {
        try {
            List<TaskDTO> tasks = taskService.getAllTasks(authentication);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    @Tag(name = "Tasks - Admin Privileges", description = "Task Operations related to Admin Privileges")
    @Operation(summary = "Create Task", description = "Create a new Task entity.")
    public ResponseEntity<TaskDTO> CreateTask(@Valid @RequestBody NewTask data, Authentication authentication)  {
        try {
            TaskDTO task = taskService.createNewTask(data, authentication);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Tasks - Admin Privileges", description = "Task Operations related to Admin Privileges")
    @Operation(summary = "Delete Any Tasks", description = "Delete a Task entity by its ID.")
    public ResponseEntity<String> deleteTask(@PathVariable Long id, Authentication authentication) {
        try {
            taskService.deleteTask(id, authentication);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.NO_CONTENT);
        } catch (TaskNotFoundException | UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    @Tag(name = "Tasks - Admin Privileges", description = "Task Operations related to Admin Privileges")
    @Operation(summary = "Update Any Task", description = "Update an existing Task entity.")
    public ResponseEntity<Object> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody NewTask updatedData,
            Authentication authentication) {
        try {
            TaskDTO task = taskService.updateTask(id, updatedData, authentication);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotFoundException | UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
