package com.usermanagement.user.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String,String>> handleUserAlreadyExist(UserAlreadyExistException ex){
        Map<String,String> response=new HashMap<>();
        response.put("error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ActivationFailedException.class)
    public ResponseEntity<Map<String,String>> handleActicationFail(ActivationFailedException ex){
        Map<String,String> response=new HashMap<>();
        response.put("error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IncompleteDTOException.class)
    public ResponseEntity<Map<String,String>> handleIncompleteDTO(IncompleteDTOException ex){
        Map<String,String> response=new HashMap<>();
        response.put("error",ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
