package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.Exercise;

import jakarta.persistence.EntityManager;


@Service
class WorkoutService {

    private final WorkoutRepository workoutRepository;

    private final WorkoutValidator workoutValidator;

    private final EntityManager entityManager;

    WorkoutService(WorkoutRepository workoutRepository, WorkoutValidator workoutValidator, EntityManager entityManager){
        this.workoutRepository = workoutRepository;
        this.workoutValidator = workoutValidator;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    PagedModel<WorkoutResponseDto> getByAccountId(Long accountId, int page, int size) {
        workoutValidator.validateByIdRequest(accountId);
        Page<WorkoutResponseDto> responsePage = workoutRepository.findByAccountId(accountId, PageRequest.of(page, size))
            .map(WorkoutMapper::entityToResponse);
        return new PagedModel<>(responsePage);
    }

    @Transactional
    WorkoutResponseDto save(WorkoutRequestDto requestDto) {
        workoutValidator.validateRequest(requestDto);
        Workout workout = WorkoutMapper.requestToEntity(requestDto);
        workout.setAccount(entityManager.getReference(Account.class, requestDto.getAccountId()));
        workout.setExercise(entityManager.getReference(Exercise.class, requestDto.getExerciseId()));
        return WorkoutMapper.entityToResponse(workoutRepository.save(workout));
    }

}
