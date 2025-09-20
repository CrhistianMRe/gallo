package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody PersonRequestDto personDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.save(personDto));
    }

    @GetMapping
    public ResponseEntity<?> viewAll(){
        return ResponseEntity.ok(personService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {
         return ResponseEntity.ok(personService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PersonRequestDto person){
        return ResponseEntity.ok(personService.update(id, person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PersonResponseDto> delete(@PathVariable Long id){
        return ResponseEntity.ok(personService.delete(id));
    }


}
