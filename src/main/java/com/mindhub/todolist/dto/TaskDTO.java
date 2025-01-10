package com.mindhub.todolist.dto;

import com.mindhub.todolist.models.Task;
import com.mindhub.todolist.models.TaskStatus;

public class TaskDTO {
    private Long id;

    private String title;
    private String description;
    private TaskStatus status;

    public TaskDTO(Task task) {
        id = task.getId();
        title = task.getTitle();
        description = task.getDescription();
        status = task.getStatus();
    }

    public TaskDTO() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
