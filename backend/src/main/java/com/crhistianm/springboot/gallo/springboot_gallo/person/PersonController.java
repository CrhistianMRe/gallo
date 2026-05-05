package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;


@RestController
@RequestMapping("api/persons")
class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    @SecurityRequirements(value = {})
    @Operation(
        responses = {
            @ApiResponse(responseCode = "404",content = {}),
        }
    )
    ResponseEntity<PersonResponseDto> create(@Valid @RequestBody PersonRequestDto personDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.save(personDto));
    }

    @GetMapping
    @Operation(
        responses = {
            @ApiResponse(responseCode = "404",content = {}),
        }
    )
    ResponseEntity<PagedModel<PersonResponseDto>> viewBy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size 
    ) {
        return ResponseEntity.ok(personService.getBy(page, size));
    }
    
    @GetMapping("/{id}")
    ResponseEntity<PersonResponseDto> viewById(@PathVariable Long id) {
         return ResponseEntity.ok(personService.getById(id));
    }

    @PutMapping("/{id}")
    ResponseEntity<PersonResponseDto> update(@PathVariable Long id, @Valid @RequestBody PersonRequestDto person){
        return ResponseEntity.ok(personService.update(id, person));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<PersonResponseDto> delete(@PathVariable Long id){
        return ResponseEntity.ok(personService.delete(id));
    }


}
