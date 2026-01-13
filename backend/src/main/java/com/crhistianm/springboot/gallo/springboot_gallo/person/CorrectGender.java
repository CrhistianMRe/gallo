package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CorrectGenderValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface CorrectGender{

    String message() default "{dto.validation.CorrectGender}";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
}
