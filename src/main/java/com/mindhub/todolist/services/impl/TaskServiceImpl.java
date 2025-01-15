package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityId;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private UserEntityService userEntityService;

    // ================= CURRENT USER ==============================

    @Override
    public TaskDTO getCurrentUserTaskDTOById(Long id, Authentication authentication)
            throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        Task task = getTaskById(id);

        if (!task.getUserEntity().getId().equals(currentUserEntity.getId())) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        return new TaskDTO(task);
    }

    @Override
    public TaskDTO currentUserCreateNewTask(NewTask newTask, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByEmail(authentication.getName()).orElse(null);
        Task task = new Task(
                newTask.title(),
                newTask.description(),
                newTask.status()
        );
        try {
            Task createdTask = saveTask(task, userEntity);
            return new TaskDTO(createdTask);
        } catch (UserEntityNotFoundException e) {
            throw new RuntimeException("User not found for task creation", e);
        }
    }

    @Override
    public List<TaskDTO> currentUserTasks(Authentication authentication) throws UserEntityNotFoundException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        return currentUserEntity.getTasks()
                .stream()
                .map(task -> new TaskDTO(task))
                .toList();
    }

    @Override
    public void currentUserDeleteTask(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        Task task = getTaskById(id);

        if (!task.getUserEntity().getId().equals(currentUserEntity.getId())) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        taskRepository.delete(task);
    }

    @Override
    public TaskDTO currentUserUpdateTask(Long id, NewTask updatedTask, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        Task task = getTaskById(id);

        if (!task.getUserEntity().getId().equals(currentUserEntity.getId())) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        task.setTitle(updatedTask.title());
        task.setDescription(updatedTask.description());
        task.setStatus(updatedTask.status());
        taskRepository.save(task);
        return new TaskDTO(task);
    }


    // ================= ADMIN PRIVILEGES ==============================

    @Override
    public TaskDTO getTaskDTOById(Long id, Authentication authentication)
            throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        Task task = getTaskById(id);

        if (currentUserEntity.getRole() != RoleType.ADMIN && !task.getUserEntity().getId().equals(currentUserEntity.getId())) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        return new TaskDTO(task);
    }

    @Override
    public Task getTaskById(Long id) throws TaskNotFoundException {
        return taskRepository.findById(id).orElseThrow( () -> new TaskNotFoundException("Task not found"));
    }

    @Override
    public Task saveTask(Task task, UserEntity taskOwner) throws UserEntityNotFoundException {
        taskOwner.addTask(task);
        taskRepository.save(task);
        return task;
    }

    @Override
    public TaskDTO createNewTask(NewTask newTask, Authentication authentication) {
        UserEntity userEntity = userEntityRepository.findByEmail(authentication.getName()).orElse(null);
        Task task = new Task(
                newTask.title(),
                newTask.description(),
                newTask.status()
        );
        try {
            UserEntity taskOwner = userEntityService.getUserEntityById(newTask.user().id());
            Task createdTask = saveTask(task, taskOwner);
            return new TaskDTO(createdTask);
        } catch (UserEntityNotFoundException e) {
            throw new RuntimeException("User not found for task creation", e);
        }
    }

    @Override
    public void deleteTask(Long id, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        Task task = getTaskById(id);

        taskRepository.delete(task);
    }

    @Override
    public List<TaskDTO> getAllTasks(Authentication authentication) throws UserEntityNotFoundException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));


        return taskRepository.findAll()
                             .stream()
                             .map( task -> new TaskDTO(task))
                             .toList();

    }

    @Override
    public TaskDTO updateTask(Long id, NewTask updatedTask, Authentication authentication) throws TaskNotFoundException, UserEntityNotFoundException, AccessDeniedException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserEntityNotFoundException("User not found"));

        if (!currentUserEntity.getRole().equals(RoleType.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this task.");
        }

        Task task = getTaskById(id);
        task.setTitle(updatedTask.title());
        task.setDescription(updatedTask.description());
        task.setStatus(updatedTask.status());
        taskRepository.save(task);
        return new TaskDTO(task);
    }
}
