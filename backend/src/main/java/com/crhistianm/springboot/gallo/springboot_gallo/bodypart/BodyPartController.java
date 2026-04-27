package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{exerciseId}")
    ResponseEntity<List<BodyPartResponseDto>> viewAllByExerciseId(@PathVariable Long exerciseId) {
        List<BodyPartResponseDto> responseList = bodyPartService.getAllByExerciseId(exerciseId);
        return ResponseEntity.ok(responseList);
    }
    
}
