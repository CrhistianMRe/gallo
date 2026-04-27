package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.ValidationServiceErrorList;

@Component
class BodyPartValidator {

    private final ExerciseValidationService exerciseValidationService;

    BodyPartValidator(ExerciseValidationService exerciseValidationService) {
        this.exerciseValidationService = exerciseValidationService;
    }

    void validateByIdRequest(Long exerciseId) {
        ValidationServiceErrorList errorList = new ValidationServiceErrorList();

        exerciseValidationService.validateExerciseExistence(exerciseId).ifPresent(error -> {
            errorList.add(error);
            errorList.throwFieldErrors();
        });

    }

}
