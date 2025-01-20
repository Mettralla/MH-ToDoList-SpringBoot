package com.mindhub.todolist.controllers;

import com.mindhub.todolist.config.JwtUtils;
import com.mindhub.todolist.dto.LoginUser;
import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @PostMapping("/login")
    @Tag(name = "Authentication Controller", description = "Authentication Operations")
    @Operation(summary = "Login User", description = "Login User and Get Auth Token.")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginUser loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    @Tag(name = "Authentication Controller", description = "Authentication Operations")
    @Operation(summary = "Register a Regular User", description = "Create new user.")
    public UserEntityDTO registerUserEntity(@Valid @RequestBody NewUserEntity newUserEntity) throws EmailAlreadyExistsException {
        if (userEntityRepository.existsByEmail(newUserEntity.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + newUserEntity.email());
        }

        UserEntity userEntity = new UserEntity(
                newUserEntity.username(),
                newUserEntity.password(),
                newUserEntity.email()
        );
        UserEntity createdUserEntity = userEntityRepository.save(userEntity);
        return new UserEntityDTO(createdUserEntity);
    }

}
