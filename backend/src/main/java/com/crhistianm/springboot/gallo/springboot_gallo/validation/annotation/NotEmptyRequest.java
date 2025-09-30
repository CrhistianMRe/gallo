package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyRequestValidator.class)
public @interface NotEmptyRequest {

    String message() default "should at least contain 1 field";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

    boolean hasSuper() default false;
    
}
