package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UniquePhoneNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumber{

    String message() default "is already registered, use another phone number";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

    
}
