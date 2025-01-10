package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/{id}")
    @Operation(summary = "Get Task By ID", description = "Retrieve a Task entity by its ID.")

    public TaskDTO getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.getTaskDTOById(id);
    }

    @GetMapping()
    @Operation(summary = "Get All Tasks", description = "Retrieve all Task entities.")
    public ResponseEntity<List<TaskDTO>> getAllUserEntities() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary = "Create Task", description = "Create a new Task entity.")
    public ResponseEntity<TaskDTO> createTask(@RequestBody NewTask data)  {
        try {
            TaskDTO task = taskService.createNewTask(data);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Tasks", description = "Delete a Task entity by its ID.")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.NO_CONTENT);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Tasks", description = "Update an existing Task entity.")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @RequestBody NewTask updatedData) {
        try {
            TaskDTO task = taskService.updateTask(id, updatedData);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
