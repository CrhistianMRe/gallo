package com.crhistianm.springboot.gallo.springboot_gallo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.WorkoutResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.WorkoutMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.WorkoutRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.WorkoutValidator;


@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;

    private final WorkoutValidator workoutValidator;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository, WorkoutValidator workoutValidator){
        this.workoutRepository = workoutRepository;
        this.workoutValidator = workoutValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<WorkoutResponseDto> getByAccountId(Long accountId, int page, int size) {
        workoutValidator.validateByIdRequest(accountId);
        Page<WorkoutResponseDto> responsePage = workoutRepository.findByAccountId(accountId, PageRequest.of(page, size))
            .map(WorkoutMapper::entityToResponse);
        return new PagedModel<>(responsePage);
    }

}
