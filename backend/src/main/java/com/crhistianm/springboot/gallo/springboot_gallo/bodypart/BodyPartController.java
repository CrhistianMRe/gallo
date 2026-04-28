package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/body-parts")
class BodyPartController {

    private final BodyPartService bodyPartService;

    BodyPartController(BodyPartService bodyPartService) {
        this.bodyPartService = bodyPartService;
    }

    @Operation(
        responses = {
            @ApiResponse(responseCode = "404", content = {})
        }
    )
    @GetMapping(name = "viewAll")
    ResponseEntity<List<BodyPartResponseDto>> viewAll() {
        return ResponseEntity.ok(bodyPartService.getAll());
    }

    @Operation(
        responses = {
            @ApiResponse(responseCode = "404", content = {})
        }
    )
    @GetMapping(name = "viewAllByExerciseId", params = {"exerciseId"})
    ResponseEntity<List<BodyPartResponseDto>> viewAllByExerciseId
    (
     @Parameter @RequestParam(required = false) Long exerciseId
    ) {
        List<BodyPartResponseDto> responseList = bodyPartService.getAllByExerciseId(exerciseId);
        return ResponseEntity.ok(responseList);
    }

}
