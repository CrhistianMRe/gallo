package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
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
        List<FieldInfoError> errorFields = new ArrayList<>();

        workoutValidationService.validateWorkoutExistence(requestDto.getWorkoutId()).ifPresent(errorFields::add);

        identityVerificationService.validateUserAllowanceByWorkoutId(requestDto.getWorkoutId()).ifPresent(errorFields::add);

        final byte LIMIT = 10;
        workoutSetValidationService.validateWorkoutSetLimit(requestDto, LIMIT).ifPresent(errorFields::add);

        if(!errorFields.isEmpty()) throw new ValidationServiceException(errorFields);
    }

}
