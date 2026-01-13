package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;

import jakarta.persistence.EntityManager;

@Service
class AccountService {

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountValidator accountValidator;

    private final AccountValidationService accountValidationService;

    private final EntityManager entityManager;

    AccountService
        (
         AccountRepository accountRepository,
         RoleRepository roleRepository,
         PasswordEncoder passwordEncoder,
         AccountValidator accountValidator,
         AccountValidationService accountValidationService,
         EntityManager entityManager
        ){
            this.accountRepository = accountRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
            this.accountValidator = accountValidator;
            this.accountValidationService = accountValidationService;
            this.entityManager = entityManager;
        }

    @Transactional
    AccountResponseDto save(AccountRequestDto accountDto) {
        accountValidator.validateRequest(accountDto);

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");


        Account account = AccountMapper.requestToEntity(accountDto);

        account.setPerson(entityManager.getReference(Person.class, accountDto.getPersonId()));
        optionalRoleUser.ifPresent(account::addRole);

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (accountDto.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(account::addRole);
        }

        return accountValidationService.settleResponseType(accountRepository.save(account));
    }

    @Transactional
    AccountResponseDto update(Long id, AccountUpdateRequestDto accountDto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        accountValidator.validateUpdateRequest(id, accountDto, account.getPerson().getId());

        if(accountDto.getPersonId() != null) account.setPerson(entityManager.getReference(Person.class, accountDto.getPersonId()));
        if(accountDto.getEmail() != null) account.setEmail(accountDto.getEmail());
        if(accountDto.isEnabled() != null) account.getAudit().setEnabled(accountDto.isEnabled());
        if(accountDto.getPassword() != null) account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        if(!accountDto.getRoles().isEmpty()) account.setRoles(accountDto.getRoles().stream().map(role -> RoleMapper.requestToEntity(role)).collect(Collectors.toList()));

        return accountValidationService.settleResponseType(accountRepository.save(account));
    }

    @Transactional
    AccountResponseDto delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        accountValidator.validateByIdRequest(account.getPerson().getId());
        accountRepository.delete(account);
        return accountValidationService.settleResponseType(account);
    }

    @Transactional(readOnly = true)
    AccountResponseDto getById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        accountValidator.validateByIdRequest(account.getPerson().getId());
        return accountValidationService.settleResponseType(account);
    }

    @Transactional(readOnly = true)
    List<AccountAdminResponseDto> getAll() {
        List<AccountAdminResponseDto> accountList = accountRepository.findAll().stream().map(a -> (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(a)).collect(Collectors.toList());
        return accountList;
    }

}
