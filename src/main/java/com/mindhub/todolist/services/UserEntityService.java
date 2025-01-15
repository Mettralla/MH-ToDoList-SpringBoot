package com.mindhub.todolist.services;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserEntityService {

    UserEntityDTO getUserEntityDTOById(Authentication authentication) throws UserEntityNotFoundException;
    UserEntity getUserEntityById(Long id) throws UserEntityNotFoundException;

    UserEntity saveUserEntity(UserEntity userEntity);
    UserEntityDTO createNewAdmin(NewUserEntity newUserEntity, Authentication authentication) throws EmailAlreadyExistsException;
    void deleteUserEntity(Authentication authentication, Long id) throws UserEntityNotFoundException;
    List<UserEntityDTO> getAllUserEntities(Authentication authentication) throws UserEntityNotFoundException;
    UserEntityDTO updateUserEntity(NewUserEntity updateUserEntityData, Authentication authentication, Long id) throws UserEntityNotFoundException;

    void deleteCurrentUser(Authentication authentication) throws UserEntityNotFoundException;
    UserEntityDTO updateCurrentUser(NewUserEntity updateUserEntityData, Authentication authentication) throws UserEntityNotFoundException;
}
