package com.mindhub.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(UserEntityNotFoundException.class)
    public ResponseEntity<String> userEntityNotFoundExceptionHandler(
            UserEntityNotFoundException userEntityNotFoundException) {
        return new ResponseEntity<>(userEntityNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> taskNotFoundExceptionHandler(
            TaskNotFoundException taskNotFoundException
    ) {
      return new ResponseEntity<>(taskNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    };
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> emailAlreadyExistsExceptionHandler(
            EmailAlreadyExistsException emailAlreadyExistsException
    ) {
        return new ResponseEntity<>(emailAlreadyExistsException.getMessage(), HttpStatus.BAD_REQUEST);
    };
}
