package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;

@Service
public class AccountValidationServiceImpl implements AccountValidationService{

    private final AccountRepository accountRepository;

    private final IdentityVerificationService identityService;

    public AccountValidationServiceImpl(AccountRepository accountRepository, IdentityVerificationService identityService) {
        this.accountRepository = accountRepository;
        this.identityService = identityService;
    }

    @Override
    public AccountResponseDto settleResponseType(Account account){
        if(identityService.isAdminAuthority()) return AccountMapper.entityToAdminResponse(account);
        return AccountMapper.entityToResponse(account);
    }

    @Override
    public Optional<FieldInfoError> validateUniqueEmail(Long accountId, AccountRequestDto accountDto){
        FieldInfoError field = null;
        if(!isEmailAvailable(accountId, accountDto.getEmail())){
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "email", "is not available, user another one!");
        }
        return Optional.ofNullable(field);
    }

    @Override
    public Optional<FieldInfoError> validatePersonAssigned(AccountRequestDto accountDto){
        FieldInfoError field = null;
        if(isPersonIdAssigned(null, accountDto.getPersonId())) {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", "is already assigned, use another person!");
        }
        return Optional.ofNullable(field);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailAvailable(Long accountId, String email) {
        if(accountId != null && accountRepository.findById(accountId).orElseThrow().getEmail().equals(email)) return true;
        return !accountRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonIdAssigned(Long accountId, Long personId){
        if(accountId != null && accountRepository.findById(accountId).orElseThrow().getPerson().getId().equals(personId)) return true;
        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(personId);
        return accountOptional.isPresent();
    }

}
