package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorMapper;

@Service
class WorkoutSetValidationService {

    private final WorkoutSetRepository workoutSetRepository;

    private final Environment environment;

    WorkoutSetValidationService
        (
         WorkoutSetRepository workoutSetRepository,
         Environment environment
        ) {
            this.workoutSetRepository = workoutSetRepository;
            this.environment = environment;
        }

    Optional<FieldInfoError> validateWorkoutSetLimit(WorkoutSetRequestDto requestDto, byte limit) {
        FieldInfoError error = null;

        if(doesWorkoutSetCountExceedLimit(requestDto.getWorkoutId(), limit, (short) requestDto.getSets().size())){
            error = FieldInfoErrorMapper.classTargetToFieldInfo(requestDto, "sets", environment.getProperty("workoutset.validation.WorkoutSetLimit"));
        }

        return Optional.ofNullable(error);
    }

    @Transactional(readOnly = true)
    boolean doesWorkoutSetCountExceedLimit(Long workoutId, byte limit, short requestSetsCount) {
        short totalCount = (short) (workoutSetRepository.countByWorkoutId(workoutId) + requestSetsCount);
        return totalCount > limit;
    }
    
}
