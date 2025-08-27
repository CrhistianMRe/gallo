package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = SelfDataTransactionValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfDataTransaction {

    String message() default "access prohibited";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

    Class<?> targetClass();
    
}
