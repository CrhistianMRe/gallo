package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
class WorkoutService {

    private final WorkoutRepository workoutRepository;

    private final WorkoutValidator workoutValidator;

    WorkoutService(WorkoutRepository workoutRepository, WorkoutValidator workoutValidator){
        this.workoutRepository = workoutRepository;
        this.workoutValidator = workoutValidator;
    }

    @Transactional(readOnly = true)
    PagedModel<WorkoutResponseDto> getByAccountId(Long accountId, int page, int size) {
        workoutValidator.validateByIdRequest(accountId);
        Page<WorkoutResponseDto> responsePage = workoutRepository.findByAccountId(accountId, PageRequest.of(page, size))
            .map(WorkoutMapper::entityToResponse);
        return new PagedModel<>(responsePage);
    }

}
