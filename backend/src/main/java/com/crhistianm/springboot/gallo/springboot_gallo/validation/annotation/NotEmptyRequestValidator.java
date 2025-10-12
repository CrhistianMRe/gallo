package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NotEmptyRequestValidator implements ConstraintValidator<NotEmptyRequest, Object>{

    private boolean hasSuper;

    @Override
    public void initialize(NotEmptyRequest constraintAnnotation) {
        this.hasSuper = constraintAnnotation.hasSuper();
    }

    private boolean isInstanceEmpty(Object classInstance, Field[] fields){
        int count = 0;
        try {
            for (Field field : fields) {
                Collection<?> collection = null;
                field.setAccessible(true);
                if(field.get(classInstance) == null) count++;
                if(field.get(classInstance) instanceof List) collection = (Collection<?>) field.get(classInstance);
                if(field.get(classInstance) instanceof Set) collection = (Collection<?>) field.get(classInstance);
                if(collection != null && collection.isEmpty()) count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count == fields.length;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null) return true;
        Field[] fields = value.getClass().getDeclaredFields();

        //if both are class and super class instances are empty
        if(hasSuper && (isInstanceEmpty(value, fields) && isInstanceEmpty(value, value.getClass().getSuperclass().getDeclaredFields()))){
            return false;
        }
        //only has base class
        if(!hasSuper && isInstanceEmpty(value, fields)) return false;
        
        return true;
    }
    
}
