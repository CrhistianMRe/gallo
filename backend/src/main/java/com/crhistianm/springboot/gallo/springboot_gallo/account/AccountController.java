package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/accounts")
class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    ResponseEntity<?> create(@Valid @RequestBody AccountRequestDto accountDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountDto));
    }

    @PatchMapping("/{id}")
    ResponseEntity<AccountResponseDto> update(@PathVariable Long id, @Validated(GroupsOrder.class) @RequestBody AccountUpdateRequestDto requestDto){
        return ResponseEntity.ok(accountService.update(id, requestDto)); 
    }

    @DeleteMapping("/{id}")
    ResponseEntity<AccountResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.delete(id));
    }

    @GetMapping("/{id}")
    ResponseEntity<AccountResponseDto> viewById(@PathVariable Long id){
        return ResponseEntity.ok(accountService.getById(id));
    }

    @GetMapping
    ResponseEntity<List<AccountAdminResponseDto>> viewAll(){
        return ResponseEntity.ok(accountService.getAll());
    }



    
}
