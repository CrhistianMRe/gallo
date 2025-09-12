package com.crhistianm.springboot.gallo.springboot_gallo.service;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountValidationService {

    AccountResponseDto settleResponseType(Account account);

    void validateRequest(AccountRequestDto accountDto);

    boolean isEmailAvailable(Long accountId, String email);

    boolean isPersonIdAssigned(Long accountId, Long personId);
    
}
