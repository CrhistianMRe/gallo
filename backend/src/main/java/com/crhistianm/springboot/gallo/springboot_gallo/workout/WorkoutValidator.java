package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseValidationService;

@Component
class WorkoutValidator {

    private final IdentityVerificationService identityService;

    private final AccountValidationService accountService;

    private final WorkoutValidationService workoutService;

    private final ExerciseValidationService exerciseService;

    WorkoutValidator
        (
         IdentityVerificationService identityService,
         AccountValidationService accountService,
         WorkoutValidationService workoutService,
         ExerciseValidationService exerciseService
        ) {
            this.workoutService = workoutService;
            this.accountService = accountService;
            this.exerciseService = exerciseService;
            this.identityService = identityService;
        }

    void validateByIdRequest(Long accountId) {
        identityService.validateAllowanceByAccountId(accountId).ifPresent(f -> {
            throw new ValidationServiceException(List.of(f));
        });
    }

    void validateRequest(WorkoutRequestDto requestDto) {
        List<FieldInfoError> errorList = new ArrayList<>();

        accountService.validateAccountRegistered(requestDto.getAccountId()).ifPresent(errorList::add);
        exerciseService.validateExerciseExistence(requestDto.getExerciseId()).ifPresent(errorList::add);

        identityService.validateAllowanceByAccountId(requestDto.getAccountId()).ifPresent(errorList::add);

        final int WORKOUT_COUNT_LIMIT = 2;
        workoutService.validatePerDayWorkoutLimit(requestDto, WORKOUT_COUNT_LIMIT).ifPresent(errorList::add);

        if(!errorList.isEmpty()) throw new ValidationServiceException(errorList);
    }
    
}
