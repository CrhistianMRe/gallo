package com.crhistianm.springboot.gallo.springboot_gallo.controller;

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
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<?> handleValidationException(Exception ex){
        int status = HttpStatus.BAD_REQUEST.value();
        Map<String, String> errors = new HashMap<String, String>();
        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) ex;
            notValidException.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), "the field " + error.getField() + " " + error.getDefaultMessage());
            });
        }
        if(ex instanceof HandlerMethodValidationException){
            HandlerMethodValidationException validationException = (HandlerMethodValidationException)ex;
            validationException.getAllErrors().stream().forEach(error ->{
                errors.put("message",  error.getDefaultMessage());
            });;
            status = HttpStatus.FORBIDDEN.value();
        }


        return ResponseEntity.status(status).body(errors);
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
