package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
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
    public Account save(AccountDto accountDto) {
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        Optional<Person> optionalPerson = personRepository.findById(accountDto.getPersonId());

        Account account = new Account(accountDto.getEmail(), accountDto.getPassword(), optionalPerson.orElseThrow());

        optionalPerson.ifPresent(account::setPerson);
        optionalRoleUser.ifPresent(account::addRole);

        if (accountDto.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(account::addRole);
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        return accountRepository.save(account);
    }
    
}
