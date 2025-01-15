package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.RoleType;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.UserEntityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserEntityServiceImpl implements UserEntityService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    public UserEntityDTO getUserEntityDTOById(Authentication authentication) throws UserEntityNotFoundException {
        UserEntity userEntity = userEntityRepository.findByEmail(authentication.getName()).orElseThrow( () ->
                new UserEntityNotFoundException("User Not Found"));
        return new UserEntityDTO(userEntity);
    }

    @Override
    public UserEntity getUserEntityById(Long id) throws UserEntityNotFoundException {
        return userEntityRepository.findById(id).orElseThrow( () -> new UserEntityNotFoundException(
                "User Not Found"));
    }

    @Override
    public UserEntity saveUserEntity(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    @Override
    public UserEntityDTO createNewAdmin(NewUserEntity newUserEntity, Authentication authentication) throws EmailAlreadyExistsException {
        UserEntity authUserEntity = userEntityRepository.findByEmail(authentication.getName()).orElse(null);
        if (userEntityRepository.existsByEmail(newUserEntity.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + newUserEntity.email());
        }

        UserEntity userEntity = new UserEntity(
                newUserEntity.username(),
                newUserEntity.password(),
                newUserEntity.email()
        );
        userEntity.setRole(RoleType.ADMIN);
        UserEntity createdUserEntity = saveUserEntity(userEntity);
        return new UserEntityDTO(createdUserEntity);
    }

    @Override
    public void deleteUserEntity(Authentication authentication, Long id) throws UserEntityNotFoundException {
        UserEntity adminUserEntity = userEntityRepository.findByEmail(authentication.getName()).orElseThrow( () -> new UserEntityNotFoundException(
                "User Not Found"));

        UserEntity deletedUserEntity = getUserEntityById(id);
        userEntityRepository.delete(deletedUserEntity);
    }

    @Override
    public List<UserEntityDTO> getAllUserEntities(Authentication authentication) throws UserEntityNotFoundException {
        UserEntity userEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow( () -> new UserEntityNotFoundException("User Not Found"));

        if (userEntity.getRole() != RoleType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Admin role required.");
        }

        return userEntityRepository.findAll()
                                   .stream()
                                   .map( user -> new UserEntityDTO(user))
                                   .toList();
    }

    @Override
    public UserEntityDTO updateUserEntity(NewUserEntity updateUserEntityData, Authentication authentication, Long id) throws UserEntityNotFoundException {
        UserEntity adminUserEntity = userEntityRepository.findByEmail(authentication.getName()).orElseThrow( () -> new UserEntityNotFoundException(
                "User Not Found"));

        UserEntity userEntity = getUserEntityById(id);

        userEntity.setUsername(updateUserEntityData.username());
        userEntity.setPassword(updateUserEntityData.password());
        userEntity.setEmail(updateUserEntityData.email());

        userEntityRepository.save(userEntity);
        return new UserEntityDTO(userEntity);
    }

    @Override
    public void deleteCurrentUser(Authentication authentication) throws UserEntityNotFoundException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName())
                .orElseThrow( () -> new UserEntityNotFoundException("User Not Found"));

        userEntityRepository.delete(currentUserEntity);
    }

    @Override
    public UserEntityDTO updateCurrentUser(NewUserEntity updateUserEntityData, Authentication authentication) throws UserEntityNotFoundException {
        UserEntity currentUserEntity = userEntityRepository.findByEmail(authentication.getName()).orElseThrow( () -> new UserEntityNotFoundException(
                "User Not Found"));

        currentUserEntity.setUsername(updateUserEntityData.username());
        currentUserEntity.setPassword(updateUserEntityData.password());
        currentUserEntity.setEmail(updateUserEntityData.email());

        userEntityRepository.save(currentUserEntity);
        return new UserEntityDTO(currentUserEntity);
    }
}
