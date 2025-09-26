package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public interface AccountValidationService {

    AccountResponseDto settleResponseType(Account account);

    boolean isEmailAvailable(Long accountId, String email);

    boolean isPersonIdAssigned(Long accountId, Long personId);
    
    public Optional<FieldInfoError> validatePersonAssigned(AccountRequestDto accountDto);

    public Optional<FieldInfoError> validateUniqueEmail(Long accountId, AccountRequestDto accountDto);

}
