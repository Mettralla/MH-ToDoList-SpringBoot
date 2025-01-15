package com.mindhub.todolist.dto;

import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.UserEntity;

import java.util.List;

public class UserEntityDTO {
    private Long id;

    private String username;

    private String email;

    private RoleType role;

    private List<TaskDTO> tasks;

    public UserEntityDTO(UserEntity userEntity) {
        id = userEntity.getId();
        username = userEntity.getUsername();
        email = userEntity.getEmail();
        role = userEntity.getRole();
        tasks = userEntity
                    .getTasks()
                    .stream()
                    .map( task -> new TaskDTO(task) )
                    .toList();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public RoleType getRole() {
        return role;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }
}
