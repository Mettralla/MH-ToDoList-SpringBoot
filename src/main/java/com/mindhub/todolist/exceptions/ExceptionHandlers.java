package com.mindhub.todolist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
