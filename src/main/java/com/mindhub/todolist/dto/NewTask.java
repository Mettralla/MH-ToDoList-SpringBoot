package com.mindhub.todolist.dto;

import com.mindhub.todolist.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTask(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Status cannot be null")
        TaskStatus status,

        UserEntityId user
) {
}
