package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.WorkoutResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.WorkoutService;

@Controller
@RequestMapping("api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<PagedModel<WorkoutResponseDto>> viewByAccountId (
            @PathVariable Long accountId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(workoutService.getByAccountId(accountId, page, size));
    }


}
