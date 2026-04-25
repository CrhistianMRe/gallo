package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ExerciseService {

    private ExerciseRepository exerciseRepository;

    ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    List<ExerciseResponseDto> getAll() {
        List<ExerciseResponseDto> responseList = exerciseRepository.findAll()
            .stream()
            .map(ExerciseMapper::entityToResponse)
            .collect(Collectors.toList());

        return responseList;
    }
    
    
}
