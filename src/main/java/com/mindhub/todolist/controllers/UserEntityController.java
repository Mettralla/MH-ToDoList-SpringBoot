package com.mindhub.todolist.controllers;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserEntityController {

    @Autowired
    private UserEntityService userEntityService;

    // ================== USERS =====================

    @GetMapping("/users/current")
    @Tag(name = "User Entity - Current User", description = "Operations related to Current User")
    @Operation(summary = "Shows Current User", description = "Retrieve Current User entity.")
    public UserEntityDTO getUserEntityById(Authentication authentication) throws UserEntityNotFoundException {
        return userEntityService.getUserEntityDTOById(authentication);
    }

    @DeleteMapping("/users/current/delete")
    @Tag(name = "User Entity - Current User", description = "Operations related to Current User")
    @Operation(summary = "Delete Current User", description = "Delete Current User entity.")
    public ResponseEntity<String> deleteCurrentUserEntity(Authentication authentication) {
        try {
            userEntityService.deleteCurrentUser(authentication);
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.NO_CONTENT);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/users/current/update")
    @Tag(name = "User Entity - Current User", description = "Operations related to Current User")
    @Operation(summary = "Update Current User", description = "Update Current User entity.")
    public ResponseEntity<UserEntityDTO> updateCurrentUserEntity(
            @Valid @RequestBody NewUserEntity updatedData,
            Authentication authentication) {
        try {
            UserEntityDTO updatedUserEntity = userEntityService.updateCurrentUser(updatedData, authentication);
            return new ResponseEntity<>(updatedUserEntity, HttpStatus.OK);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // ================== ADMIN =====================

    @GetMapping("/admin/users")
    @Tag(name = "User Entity - Admin Privileges", description = "Operations related to Admin Privileges")
    @Operation(summary = "Get All Users", description = "Admin can Retrieve all User entities.")
    public ResponseEntity<Object> getAllUserEntities(Authentication authentication) {
        try {
            List<UserEntityDTO> users = userEntityService.getAllUserEntities(authentication);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/new")
    @Tag(name = "User Entity - Admin Privileges", description = "Operations related to Admin Privileges")
    @Operation(summary = "Create New Admin", description = "Admin can Create a New Admin User entity.")
    public ResponseEntity<Object> createNewAdmin(@Valid @RequestBody NewUserEntity data, Authentication authentication) {
        try {
            UserEntityDTO createdAdmin = userEntityService.createNewAdmin(data, authentication);
            return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/admin/users/{id}")
    @Tag(name = "User Entity - Admin Privileges", description = "Operations related to Admin Privileges")
    @Operation(summary = "Delete Any User", description = "Admin can delete an User entity by its ID.")
    public ResponseEntity<String> deleteUserEntity(Authentication authentication, @PathVariable Long id) {
        try {
            userEntityService.deleteUserEntity(authentication, id);
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.NO_CONTENT);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/admin/users/{id}")
    @Tag(name = "User Entity - Admin Privileges", description = "Operations related to Admin Privileges")
    @Operation(summary = "Update Any User", description = "Admin can Update an existing User entity.")
    public ResponseEntity<UserEntityDTO> updateUserEntity(
            @Valid @RequestBody NewUserEntity updatedData,
            Authentication authentication,
            @PathVariable Long id) {
        try {
            UserEntityDTO updatedUserEntity = userEntityService.updateUserEntity(updatedData, authentication, id);
            return new ResponseEntity<>(updatedUserEntity, HttpStatus.OK);
        } catch (UserEntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
