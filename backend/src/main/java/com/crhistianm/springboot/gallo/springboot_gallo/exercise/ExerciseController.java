package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/exercises")
class ExerciseController {

    private final ExerciseService exerciseService;

    ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    @Operation(
        responses = {
            @ApiResponse(responseCode = "404",content = {}),
        }
    )
    ResponseEntity<List<ExerciseResponseDto>> viewAll() {
        return ResponseEntity.ok(exerciseService.getAll());
    }
    
}
