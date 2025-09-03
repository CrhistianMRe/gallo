package com.crhistianm.springboot.gallo.springboot_gallo.service;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountService {

    AccountResponseDto settleResponseType(Account account);

    AccountResponseDto save(AccountRequestDto accountDto);

    AccountResponseDto getById(Long id);
    
    boolean isEmailAvailable(String email);

    boolean isPersonIdAssigned(Long personId);
}
