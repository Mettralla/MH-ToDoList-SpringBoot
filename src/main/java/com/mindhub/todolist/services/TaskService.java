package com.mindhub.todolist.services;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityId;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.Task;

import java.util.List;

public interface TaskService {
    TaskDTO getTaskDTOById(Long id) throws TaskNotFoundException;
    Task getTaskById(Long id) throws TaskNotFoundException;
    Task saveTask(Task task, UserEntityId userId) throws UserEntityNotFoundException;
    TaskDTO createNewTask(NewTask newTask);
    void deleteTask(Long id) throws TaskNotFoundException;
    List<TaskDTO> getAllTasks();
    TaskDTO updateTask(Long id, NewTask updatedTask) throws TaskNotFoundException;
}
