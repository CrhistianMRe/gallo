package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.RoleMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.AccountValidator;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final PersonRepository personRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountValidator accountValidator;

    private final AccountValidationService validationService;

    private final IdentityVerificationService identityService;

    public AccountServiceImpl(AccountRepository accountRepository, PersonRepository personRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AccountValidator accountValidator, AccountValidationService validationService, IdentityVerificationService identityService){
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountValidator = accountValidator;
        this.validationService = validationService;
        this.identityService = identityService;
    }

    @Transactional
    @Override
    public AccountResponseDto save(AccountRequestDto accountDto) {
        accountValidator.validateRequest(accountDto);

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        System.out.println(accountDto);

        Optional<Person> optionalPerson = personRepository.findById(accountDto.getPersonId());

        Account account = AccountMapper.requestToEntity(accountDto);

        optionalPerson.ifPresent(account::setPerson);
        optionalRoleUser.ifPresent(System.out::println);
        optionalRoleUser.ifPresent(account::addRole);

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (accountDto.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(account::addRole);
        }

        return validationService.settleResponseType(accountRepository.save(account));
    }

    @Transactional
    @Override
    public AccountResponseDto update(Long id, AccountUpdateRequestDto accountDto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        accountValidator.validateUpdateRequest(id, accountDto, account.getPerson().getId());

        if(accountDto.getPersonId() != null) account.setPerson(personRepository.findById(accountDto.getPersonId()).orElseThrow(() -> new NotFoundException(Person.class)));
        if(accountDto.getEmail() != null) account.setEmail(accountDto.getEmail());
        if(accountDto.isEnabled() != null) account.getAudit().setEnabled(accountDto.isEnabled());
        if(accountDto.getPassword() != null) account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        if(!accountDto.getRoles().isEmpty()) account.setRoles(accountDto.getRoles().stream().map(role -> RoleMapper.requestToEntity(role)).collect(Collectors.toList()));

        return validationService.settleResponseType(accountRepository.save(account));
    }

    @Transactional
    @Override
    public AccountResponseDto getById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        identityService.validateUserAllowance(account.getPerson().getId()).ifPresent(f ->{
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        return validationService.settleResponseType(account);
    }

    @Override
    @Transactional
    public List<AccountAdminResponseDto> getAll() {
        List<AccountAdminResponseDto> accountList = accountRepository.findAll().stream().map(a -> (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(a)).collect(Collectors.toList());
        return accountList;
    }

}
