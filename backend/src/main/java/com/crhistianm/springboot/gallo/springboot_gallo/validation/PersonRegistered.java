package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PersonRegisteredValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonRegistered{

    String message() default "is not registered yet, register person first";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
}
