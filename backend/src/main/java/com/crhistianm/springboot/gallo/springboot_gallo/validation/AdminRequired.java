package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = AdminRequiredValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminRequired {

    String message() default "requires an admin user!";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

}
