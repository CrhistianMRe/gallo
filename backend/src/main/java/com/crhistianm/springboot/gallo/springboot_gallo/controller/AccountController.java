package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody AccountRequestDto accountDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponseDto> update(@PathVariable Long id, @RequestBody AccountUpdateRequestDto requestDto){
        return ResponseEntity.ok(accountService.update(id, requestDto)); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> viewById(@PathVariable Long id){
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AccountAdminResponseDto>> viewAll(){
        return ResponseEntity.ok(accountService.getAll());
    }



    
}
