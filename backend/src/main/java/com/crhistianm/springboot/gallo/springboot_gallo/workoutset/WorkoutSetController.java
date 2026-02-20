package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/workout-sets")
class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @PostMapping
    ResponseEntity<List<WorkoutSetResponseDto>> createAll(@RequestBody WorkoutSetRequestDto requestDto) {
        List<WorkoutSetResponseDto> responseDto = workoutSetService.saveAll(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
