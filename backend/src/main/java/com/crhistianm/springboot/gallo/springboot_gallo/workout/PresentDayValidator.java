package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PresentDayValidator implements ConstraintValidator<PresentDay, LocalDate> { 

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate todayDate = LocalDate.now();
        return todayDate.isEqual(value);
    }

}
