package com.crhistianm.springboot.gallo.springboot_gallo.service;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;

public interface AccountService {

    AccountResponseDto save(AccountCreateDto accountDto);
    
}
