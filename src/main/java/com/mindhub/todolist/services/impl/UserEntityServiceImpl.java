package com.mindhub.todolist.services.impl;

import com.mindhub.todolist.dto.NewUserEntity;
import com.mindhub.todolist.dto.TaskDTO;
import com.mindhub.todolist.dto.UserEntityDTO;
import com.mindhub.todolist.exceptions.EmailAlreadyExistsException;
import com.mindhub.todolist.exceptions.UserEntityNotFoundException;
import com.mindhub.todolist.models.UserEntity;
import com.mindhub.todolist.repositories.UserEntityRepository;
import com.mindhub.todolist.services.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEntityServiceImpl implements UserEntityService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Override
    public UserEntityDTO getUserEntityDTOById(Long id) throws UserEntityNotFoundException {
        return new UserEntityDTO(getUserEntityById(id));
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
    public UserEntityDTO createNewUserEntity(NewUserEntity newUserEntity) throws EmailAlreadyExistsException {
        if (userEntityRepository.existsByEmail(newUserEntity.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + newUserEntity.email());
        }

        UserEntity userEntity = new UserEntity(
                newUserEntity.username(),
                newUserEntity.password(),
                newUserEntity.email()
        );
        UserEntity createdUserEntity = saveUserEntity(userEntity);
        return new UserEntityDTO(createdUserEntity);
    }

    @Override
    public void deleteUserEntity(Long id) throws UserEntityNotFoundException {
        UserEntity userEntity = getUserEntityById(id);
        userEntityRepository.delete(userEntity);
    }

    @Override
    public List<UserEntityDTO> getAllUserEntities() {
        return userEntityRepository.findAll()
                                   .stream()
                                   .map( userEntity -> new UserEntityDTO(userEntity))
                                   .toList();
    }

    @Override
    public UserEntityDTO updateUserEntity(Long id, NewUserEntity updateUserEntityData) throws UserEntityNotFoundException {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setUsername(updateUserEntityData.username());
        userEntity.setPassword(updateUserEntityData.password());
        userEntity.setEmail(updateUserEntityData.email());
        userEntityRepository.save(userEntity);
        return new UserEntityDTO(userEntity);
    }


}
