package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "login-controller")
@RequestMapping("/api/auth")
class LoginDocController {

    @SecurityRequirements(value = {})
    @Operation(
    requestBody = @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(
                type = "object",
                requiredProperties = {"email", "password"},
                properties = {
                    @StringToClassMapItem(key = "email", value = String.class),
                    @StringToClassMapItem(key = "password", value = String.class)
                })
            )
        ),
    responses = {
        @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SuccesfulAuthResponseDto.class))),
        @ApiResponse(responseCode = "400", content = {}),
        @ApiResponse(responseCode = "404", content = {}),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(example = "{\"message\": \"authentication message\"}")))
        }
    )
    @PostMapping("/login")
    void login(){};

}
