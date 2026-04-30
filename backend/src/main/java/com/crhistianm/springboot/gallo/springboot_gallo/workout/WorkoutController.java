package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import org.springframework.data.web.PagedModel;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/workouts")
class WorkoutController {

    private final WorkoutService workoutService;

    WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    @Operation(
        responses = {
            @ApiResponse(
                responseCode = "404",
                content = {}
            )
        }
    )
    ResponseEntity<PagedModel<WorkoutResponseDto>> viewByAccountId (
            @RequestParam(required = true) Long accountId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(workoutService.getByAccountId(accountId, page, size));
    }

    @PostMapping
    @Operation(
        responses = {
            @ApiResponse(
                responseCode = "404",
                content = {}
            )
        }
    )
    ResponseEntity<WorkoutResponseDto> create(@RequestBody @Validated (GroupsOrder.class) WorkoutRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workoutService.save(requestDto));
    }

}
