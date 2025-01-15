package com.mindhub.todolist.services;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityId;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.UserEntity;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TaskService {
    TaskDTO getTaskDTOById(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
    TaskDTO getCurrentUserTaskDTOById(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
    Task getTaskById(Long id) throws TaskNotFoundException;
    Task saveTask(Task task, UserEntity taskOwner) throws UserEntityNotFoundException;
    TaskDTO createNewTask(NewTask newTask, Authentication authentication);
    TaskDTO currentUserCreateNewTask(NewTask newTask, Authentication authentication);
    void deleteTask(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
    void currentUserDeleteTask(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
    List<TaskDTO> getAllTasks(Authentication authentication) throws UserEntityNotFoundException;
    List<TaskDTO> currentUserTasks(Authentication authentication) throws UserEntityNotFoundException;
    TaskDTO currentUserUpdateTask(Long id, NewTask updatedTask, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
    TaskDTO updateTask(Long id, NewTask updatedTask, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException;
}
