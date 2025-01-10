package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.services.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserEntityController {

    @Autowired
    private UserEntityService userEntityService;

    @GetMapping("/{id}")
    @Operation(summary = "Get User By ID", description = "Retrieve a User entity by its ID.")
    public UserEntityDTO getUserEntityById(@PathVariable Long id) throws UserEntityNotFoundException {
        return userEntityService.getUserEntityDTOById(id);
    }

    @GetMapping()
    @Operation(summary = "Get All Users", description = "Retrieve all User entities.")
    public ResponseEntity<List<UserEntityDTO>> getAllUserEntities() {
        List<UserEntityDTO> users = userEntityService.getAllUserEntities();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary = "Create User", description = "Create a new User entity.")
    public ResponseEntity<Object> createUserEntity(@RequestBody NewUserEntity data) {
        try {
            UserEntityDTO createdUserEntity = userEntityService.createNewUserEntity(data);
            return new ResponseEntity<>(createdUserEntity, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete User", description = "Delete a User entity by its ID.")
    public ResponseEntity<String> deleteUserEntity(@PathVariable Long id) {
        try {
            userEntityService.deleteUserEntity(id);
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.NO_CONTENT);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User", description = "Update an existing User entity.")
    public ResponseEntity<UserEntityDTO> updateUserEntity(
            @PathVariable Long id,
            @RequestBody NewUserEntity updatedData) {
        try {
            UserEntityDTO updatedUserEntity = userEntityService.updateUserEntity(id, updatedData);
            return new ResponseEntity<>(updatedUserEntity, HttpStatus.OK);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
