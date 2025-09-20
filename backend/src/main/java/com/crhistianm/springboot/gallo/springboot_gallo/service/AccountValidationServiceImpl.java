package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;

@Service
public class AccountValidationServiceImpl implements AccountValidationService{

    private final AccountRepository accountRepository;

    private final PersonValidationService personService;

    private final IdentityVerificationService identityService;

    public AccountValidationServiceImpl(AccountRepository accountRepository, PersonValidationService personService, IdentityVerificationService identityService) {
        this.accountRepository = accountRepository;
        this.personService = personService;
        this.identityService = identityService;
    }

    @Override
    public AccountResponseDto settleResponseType(Account account){
        if(identityService.isAdminAuthority()) return AccountMapper.entityToAdminResponse(account);
        return AccountMapper.entityToResponse(account);
    }

    @Override
    public void validateRequest(AccountRequestDto accountDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        if(!personService.isPersonRegistered(accountDto.getPersonId())) {
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", "is not registered, register first!"));
        }
        //Null id as is account creation
        if(isPersonIdAssigned(null, accountDto.getPersonId())) {
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", "is already assigned, use another person!"));
        }
        //Null id as is account creation
        if(!isEmailAvailable(null, accountDto.getEmail())){
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "email", "is not available, user another one!"));
        }
        if(accountDto.isAdmin() == true && !identityService.isAdminAuthority()){
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "admin", "requires an admin user!"));
        } 
        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
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
