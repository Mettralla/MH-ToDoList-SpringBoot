package com.mindhub.todolist.services;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.UserEntity;

import java.util.List;

public interface UserEntityService {

    UserEntityDTO getUserEntityDTOById(Long id) throws UserEntityNotFoundException;
    UserEntity getUserEntityById(Long id) throws UserEntityNotFoundException;

    UserEntity saveUserEntity(UserEntity userEntity);
    UserEntityDTO createNewUserEntity(NewUserEntity newUserEntity) throws EmailAlreadyExistsException;
    void deleteUserEntity(Long id) throws UserEntityNotFoundException;
    List<UserEntityDTO> getAllUserEntities();
    UserEntityDTO updateUserEntity(Long id, NewUserEntity updateUserEntityData) throws UserEntityNotFoundException;

}
