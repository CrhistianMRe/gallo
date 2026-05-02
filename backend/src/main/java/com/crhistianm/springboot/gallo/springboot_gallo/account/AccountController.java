package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/accounts")
class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @SecurityRequirements(value = {})
    @Operation( 
        responses = {
            @ApiResponse(responseCode = "404",content = {}), 
            @ApiResponse
            (
             responseCode = "201",
             content = @Content(
                 schema = @Schema(
                     type = "object",
                     oneOf = {AccountUserResponseDto.class, AccountAdminResponseDto.class}
                     ))
            )
        }
    )
    ResponseEntity<AccountResponseDto> create(@Valid @RequestBody AccountRequestDto accountDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountDto));
    }

    @PatchMapping("/{id}")
    @AccountApiResponse200
    ResponseEntity<AccountResponseDto> update(@PathVariable Long id, @Validated(GroupsOrder.class) @RequestBody AccountUpdateRequestDto requestDto){
        return ResponseEntity.ok(accountService.update(id, requestDto)); 
    }

    @DeleteMapping("/{id}")
    @AccountApiResponse200
    ResponseEntity<AccountResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.delete(id));
    }

    @GetMapping("/{id}")
    @AccountApiResponse200
    ResponseEntity<AccountResponseDto> viewById(@PathVariable Long id){
        return ResponseEntity.ok(accountService.getById(id));
    }

    @Operation( 
        responses = {
            @ApiResponse(responseCode = "404",content = {}),
            @ApiResponse(responseCode = "400",content = {})
        }
    )
    @GetMapping
    ResponseEntity<PagedModel<AccountAdminResponseDto>> viewBy (
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(accountService.getBy(page, size));
    }
   
}
