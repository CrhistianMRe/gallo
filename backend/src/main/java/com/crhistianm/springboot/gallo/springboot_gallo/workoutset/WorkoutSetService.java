
package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.workout.Workout;

import jakarta.persistence.EntityManager;

@Service
class WorkoutSetService {

    private final WorkoutSetRepository workoutSetRepository;

    private final EntityManager entityManager;

    WorkoutSetService(WorkoutSetRepository workoutSetRepository, EntityManager entityManager) {
        this.workoutSetRepository = workoutSetRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    List<WorkoutSetResponseDto> saveAll(WorkoutSetRequestDto requestDto) {
        List<WorkoutSet> entityList = requestDto.getSets().stream().map(WorkoutSetMapper::dtoToEntity).collect(Collectors.toList());

        Workout workout = entityManager.getReference(Workout.class, requestDto.getWorkoutId());
        entityList.forEach(set -> set.setWorkout(workout));

        List<WorkoutSet> persistedList = (List<WorkoutSet>) workoutSetRepository.saveAll(entityList);

        List<WorkoutSetResponseDto> responseDto = persistedList.stream().map(WorkoutSetMapper::entityToResponse).collect(Collectors.toList());

        return responseDto;
    }


}
