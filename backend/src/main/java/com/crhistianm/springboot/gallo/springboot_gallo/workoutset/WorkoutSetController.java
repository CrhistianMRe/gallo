package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

@RestController
@RequestMapping("api/workout-sets")
class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    WorkoutSetController(WorkoutSetService workoutSetService) {
        this.workoutSetService = workoutSetService;
    }

    @PostMapping
    ResponseEntity<List<WorkoutSetResponseDto>> createAll(@Validated (GroupsOrder.class) @RequestBody WorkoutSetRequestDto requestDto) {
        List<WorkoutSetResponseDto> responseDto = workoutSetService.saveAll(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    ResponseEntity<List<WorkoutSetResponseDto>> viewAllByWorkoutId(@RequestParam(required = true) Long workoutId) {
        List<WorkoutSetResponseDto> responseList = workoutSetService.getAllByWorkoutId(workoutId);
        return ResponseEntity.ok(responseList);
    }

}
