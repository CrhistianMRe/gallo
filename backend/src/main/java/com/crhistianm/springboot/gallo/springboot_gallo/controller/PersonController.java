package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import java.util.Optional;

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
    public ResponseEntity<?> viewById(@PathVariable Long id){
        Optional<PersonResponseDto> responseOptional = personService.getById(id);
        if(responseOptional.isPresent()) return ResponseEntity.ok(personService.getById(id).orElseThrow());
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody PersonRequestDto person, @PathVariable Long id){

        Optional<PersonResponseDto> responseDto = personService.update(id, person);

        if (responseDto.isPresent()) return ResponseEntity.ok(responseDto.orElseThrow());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PersonResponseDto> delete(@PathVariable Long id){
        Optional<PersonResponseDto> responseDto = personService.delete(id);
        if(responseDto.isPresent()) return ResponseEntity.ok(responseDto.orElseThrow());
        return ResponseEntity.notFound().build();
    }


}
