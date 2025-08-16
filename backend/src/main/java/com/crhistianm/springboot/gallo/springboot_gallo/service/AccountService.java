package com.crhistianm.springboot.gallo.springboot_gallo.service;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;

public interface AccountService {

    AccountResponseDto save(AccountRequestDto accountDto);
    
    boolean isEmailAvailable(String email);

    boolean isPersonIdAssigned(Long personId);
}
