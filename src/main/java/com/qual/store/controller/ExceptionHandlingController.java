package com.qual.store.controller;

import com.qual.store.exceptions.InvalidOrderStatusException;
import com.qual.store.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<Object> handlerCompanyException(InvalidOrderStatusException exception) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("error message", exception.getLocalizedMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handlerProductNotFoundException(ProductNotFoundException exception) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("error message", exception.getLocalizedMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }
}
