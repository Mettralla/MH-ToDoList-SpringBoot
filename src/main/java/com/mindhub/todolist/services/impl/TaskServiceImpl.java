package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dto.NewTask;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityId;
import com.mindhub.todolist.exceptions.TaskNotFoundException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.TaskRepository;
import com.mindhub.todolist.services.TaskService;
import com.mindhub.todolist.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserEntityService userEntityService;

    @Override
    public TaskDTO getTaskDTOById(Long id) throws TaskNotFoundException {
        return new TaskDTO(getTaskById(id));
    }

    @Override
    public Task getTaskById(Long id) throws TaskNotFoundException {
        return taskRepository.findById(id).orElseThrow( () -> new TaskNotFoundException("Task not found"));
    }

    @Override
    public Task saveTask(Task task, UserEntityId userId) throws UserEntityNotFoundException {
        UserEntity taskOwner = userEntityService.getUserEntityById(userId.id());
        taskOwner.addTask(task);
        taskRepository.save(task);
        return task;
    }

    @Override
    public TaskDTO createNewTask(NewTask newTask) {
        Task task = new Task(
                newTask.title(),
                newTask.description(),
                newTask.status()
        );
        try {
            Task createdTask = saveTask(task, newTask.user());
            return new TaskDTO(createdTask);
        } catch (UserEntityNotFoundException e) {
            throw new RuntimeException("User not found for task creation", e);
        }
    }

    @Override
    public void deleteTask(Long id) throws TaskNotFoundException {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                             .stream()
                             .map( task -> new TaskDTO(task))
                             .toList();
    }

    @Override
    public TaskDTO updateTask(Long id, NewTask updatedTask) throws TaskNotFoundException {
        Task task = getTaskById(id);
        task.setTitle(updatedTask.title());
        task.setDescription(updatedTask.description());
        task.setStatus(updatedTask.status());
        taskRepository.save(task);
        return new TaskDTO(task);
    }
}
