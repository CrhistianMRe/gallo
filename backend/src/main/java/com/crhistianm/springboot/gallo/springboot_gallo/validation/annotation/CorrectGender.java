package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CorrectGenderValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectGender{

    String message() default "is not a valid gender, use M, F or NT";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
}
