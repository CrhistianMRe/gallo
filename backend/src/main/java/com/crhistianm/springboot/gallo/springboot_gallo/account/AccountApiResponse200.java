package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
responseCode = "200",
content = @Content(
    schema = @Schema(
        type = "object",
        oneOf = {AccountUserResponseDto.class, AccountAdminResponseDto.class}
        ))
)
@interface AccountApiResponse200{}
