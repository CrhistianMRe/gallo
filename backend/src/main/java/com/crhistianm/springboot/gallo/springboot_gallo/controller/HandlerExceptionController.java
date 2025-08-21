package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "the field " + error.getField() + " " + error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleJsonParserException(DateTimeParseException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParsedString(), "not valid, use yyyy/mm/dd");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFoundException(NotFoundException ex){
        Map<String, Object> errors = new HashMap<>();
        errors.put("date", new Date().toString());
        errors.put("message", ex.getMessage());
        errors.put("status", HttpStatus.NOT_FOUND.value());
        return errors;
    }
    
}
