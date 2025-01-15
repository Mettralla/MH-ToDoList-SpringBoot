package com.mindhub.todolist.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginUser(
        @NotNull(message = "Email cannot be null")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is invalid")
        String email,

        @NotNull(message = "Password cannot be null")
        @NotEmpty(message = "Password cannot be empty")
        String password
) {}
