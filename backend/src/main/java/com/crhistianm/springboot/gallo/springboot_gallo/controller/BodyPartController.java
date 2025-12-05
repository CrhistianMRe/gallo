package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.BodyPartResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.BodyPartService;

@Controller
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
