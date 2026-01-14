package com.crhistianm.springboot.gallo.springboot_gallo.shared.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> classObject) {
        
        super(classObject.getSimpleName() + " not found");
    }
    
}
