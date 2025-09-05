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
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final PersonRepository personRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final IdentityVerificationService identityService;

    public AccountServiceImpl(AccountRepository accountRepository, PersonRepository personRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, IdentityVerificationService identityService){
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.identityService = identityService;
    }

    public AccountResponseDto settleResponseType(Account account){
        if(identityService.isAdminAuthority()) return AccountMapper.entityToAdminResponse(account);
        return AccountMapper.entityToResponse(account);
    }

    @Transactional
    @Override
    public AccountResponseDto save(AccountRequestDto accountDto) {
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
            return AccountMapper.entityToAdminResponse(accountRepository.save(account));
        }

        return AccountMapper.entityToResponse(accountRepository.save(account));
    }

    @Transactional
    @Override
    public AccountResponseDto getById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        return settleResponseType(account);
    }

    @Override
    @Transactional
    public List<AccountAdminResponseDto> getAll() {
        List<AccountAdminResponseDto> accountList = accountRepository.findAll().stream().map(a -> (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(a)).collect(Collectors.toList());
        return accountList;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailAvailable(String email) {
        return !accountRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonIdAssigned(Long personId){
        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(personId);
        return accountOptional.isPresent();
    }
    
}
