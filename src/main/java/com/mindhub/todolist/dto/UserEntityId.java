package com.mindhub.todolist.dto;

import jakarta.validation.constraints.NotNull;

public record UserEntityId(
        @NotNull(message = "User ID cannot be null")
        Long id
) {}
