package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public AccountResponseDto save(AccountCreateDto accountDto) {
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        System.out.println(accountDto);

        Optional<Person> optionalPerson = personRepository.findById(accountDto.getPersonId());

        Account account = AccountMapper.createToEntity(accountDto);

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

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailAvailable(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonIdAssigned(Long personId){
        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(personId);
        if(accountOptional.isPresent()){
            return true;
        }

        return false;

    }
    
}
