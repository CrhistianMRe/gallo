package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("api/body-parts")
class BodyPartController {

    private final BodyPartService bodyPartService;

    BodyPartController(BodyPartService bodyPartService) {
        this.bodyPartService = bodyPartService;
    }

    @GetMapping
    ResponseEntity<List<BodyPartResponseDto>> viewAll() {
        return ResponseEntity.ok(bodyPartService.getAll());
    }

    @GetMapping(params = {"exerciseId"})
    @Operation(
        responses = {
            @ApiResponse(responseCode = "404", content = {})
        }
    )
    ResponseEntity<List<BodyPartResponseDto>> viewAllByExerciseId(@RequestParam(required = true) Long exerciseId) {
        List<BodyPartResponseDto> responseList = bodyPartService.getAllByExerciseId(exerciseId);
        return ResponseEntity.ok(responseList);
    }
    //Tenes que ver como arreglar esto juntando ambos o viendo como
    
}
