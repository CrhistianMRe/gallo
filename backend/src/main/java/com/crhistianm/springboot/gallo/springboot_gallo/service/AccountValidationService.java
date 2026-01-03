package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;

@Service
public class AccountValidationService {

    private final AccountRepository accountRepository;

    private final IdentityVerificationService identityService;

    private final Environment env;

    public AccountValidationService(AccountRepository accountRepository, IdentityVerificationService identityService, Environment env) {
        this.accountRepository = accountRepository;
        this.identityService = identityService;
        this.env = env;
    }

    public AccountResponseDto settleResponseType(Account account){
        if(identityService.isAdminAuthority()) return AccountMapper.entityToAdminResponse(account);
        return AccountMapper.entityToResponse(account);
    }

    public Optional<FieldInfoError> validateUniqueEmail(Long accountId, AbstractAccountRequestDto accountDto){
        FieldInfoError field = null;
        if(!isEmailAvailable(accountId, accountDto.getEmail())){
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "email", env.getProperty("account.validation.UniqueEmail"));
        }
        return Optional.ofNullable(field);
    }

    public Optional<FieldInfoError> validatePersonAssigned(Long pathId, AbstractAccountRequestDto accountDto){
        FieldInfoError field = null;
        if(isPersonIdAssigned(pathId, accountDto.getPersonId())) {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", env.getProperty("account.validation.PersonAssigned"));
        }
        return Optional.ofNullable(field);
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(Long accountId, String email) {
        if(accountId != null && accountRepository.findById(accountId).orElseThrow().getEmail().equals(email)) return true;
        return !accountRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isPersonIdAssigned(Long accountId, Long personId){
        if(accountId != null && accountRepository.findById(accountId).orElseThrow().getPerson().getId().equals(personId)) return false;
        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(personId);
        return accountOptional.isPresent();
    }

}
