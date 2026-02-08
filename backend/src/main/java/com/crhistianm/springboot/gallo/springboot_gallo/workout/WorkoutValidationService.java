package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import static com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorMapper.classTargetToFieldInfo;

@Service
class WorkoutValidationService {

    private final WorkoutRepository workoutRepository;

    private final Environment env;

    WorkoutValidationService(WorkoutRepository workoutRepository, Environment env) {
        this.workoutRepository = workoutRepository;
        this.env = env;
    }

    @Transactional(readOnly = true)
    boolean doesWorkoutCountExceedLimit(Long accountId, LocalDate date, Long exerciseId, int limit) {
        int workoutCount = workoutRepository.countPerDayByAccountAndExercise(accountId, date, exerciseId);
        return workoutCount >= limit;
    }

    Optional<FieldInfoError> validatePerDayWorkoutLimit(WorkoutRequestDto requestDto, int limit) {
        FieldInfoError field = null;

        Long accountId = requestDto.getAccountId();
        LocalDate workoutDate = requestDto.getWorkoutDate();
        Long exerciseId = requestDto.getExerciseId();

        if(doesWorkoutCountExceedLimit(accountId, workoutDate, exerciseId, limit)){
            field = classTargetToFieldInfo(requestDto, "workoutDate", env.getProperty("workout.validation.PerDayWorkoutLimit"));
        }

        return Optional.ofNullable(field);
    }

}
