package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorMapper.classTargetToFieldInfo;

@Service
public class WorkoutValidationService {

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

    @Transactional(readOnly = true)
    boolean workoutExists(Long workoutId) {
        return workoutRepository.existsById(workoutId);
    }

    public Optional<FieldInfoError> validateWorkoutExistence(Long workoutId) {
        FieldInfoError field = null;

        if(!workoutExists(workoutId)) {
            field = new FieldInfoErrorBuilder()
                .name("workoutId")
                .value(workoutId)
                .type(workoutId.getClass())
                .errorMessage(env.getProperty("workout.validation.WorkoutExistence"))
                .build();
        }

        return Optional.ofNullable(field);
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
