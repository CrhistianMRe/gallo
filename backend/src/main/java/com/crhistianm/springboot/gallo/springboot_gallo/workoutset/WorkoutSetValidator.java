package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.ValidationServiceErrorList;
import com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutValidationService;

@Component
class WorkoutSetValidator {

    private final WorkoutSetValidationService workoutSetValidationService;

    private final WorkoutValidationService workoutValidationService;
    
    private final IdentityVerificationService identityVerificationService;

    WorkoutSetValidator
        (
         WorkoutSetValidationService workoutSetValidationService,
         WorkoutValidationService workoutValidationService,
         IdentityVerificationService identityVerificationService
        ) {
            this.workoutSetValidationService = workoutSetValidationService;
            this.workoutValidationService = workoutValidationService;
            this.identityVerificationService = identityVerificationService;
        }

    void validateSaveAllRequest(WorkoutSetRequestDto requestDto) {
        ValidationServiceErrorList errorFields = new ValidationServiceErrorList();

        workoutValidationService.validateWorkoutExistence(requestDto.getWorkoutId()).ifPresent(error -> {
            errorFields.add(error);
            errorFields.throwFieldErrors();
        });

        identityVerificationService.validateUserAllowanceByWorkoutId(requestDto.getWorkoutId()).ifPresent(errorFields::add);

        final byte LIMIT = 10;
        workoutSetValidationService.validateWorkoutSetLimit(requestDto, LIMIT).ifPresent(errorFields::add);

        errorFields.throwFieldErrors();
    }

}
