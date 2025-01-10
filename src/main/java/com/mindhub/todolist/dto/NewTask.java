package com.mindhub.todolist.dto;

import com.mindhub.todolist.models.TaskStatus;

public record NewTask(String title, String description, TaskStatus status, UserEntityId user) {
}
