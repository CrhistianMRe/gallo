package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.BodyPartResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.BodyPartService;

@RestController
@RequestMapping("api/body-parts")
public class BodyPartController {

    private final BodyPartService bodyPartService;

    public BodyPartController(BodyPartService bodyPartService) {
        this.bodyPartService = bodyPartService;
    }

    @GetMapping
    public ResponseEntity<List<BodyPartResponseDto>> viewAll() {
        return ResponseEntity.ok(bodyPartService.getAll());
    }
    
}
