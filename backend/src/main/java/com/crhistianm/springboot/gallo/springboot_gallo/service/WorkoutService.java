package com.crhistianm.springboot.gallo.springboot_gallo.service;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.WorkoutResponseDto;

import org.springframework.data.web.PagedModel;

public interface WorkoutService {

    PagedModel<WorkoutResponseDto> getByAccountId(Long accountId, int page, int size);
    
}
