package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PresentDayValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface PresentDay {

    String message() default "{dto.validation.PresentDay}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
