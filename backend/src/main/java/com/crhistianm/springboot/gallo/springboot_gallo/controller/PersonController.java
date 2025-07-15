package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;


@RestController
@RequestMapping("api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/register")
    public ResponseEntity<?> create(@RequestBody PersonCreateDto personDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.save(personDto));
    }

    
}
