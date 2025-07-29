package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;

@Service
public class AccountUserDetailsService implements UserDetailsService{

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByEmailWithRoles(email);

        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Email %s does not exist in db", email));
        }

        Account account = accountOptional.orElseThrow();

        System.out.println("----------------------------------------------------" + account);
        
        //get roles
        List<GrantedAuthority> authorities = account.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());


        return new CustomAccountUserDetails(
                account.getPassword(), 
                account.getEmail(), 
                account.getEmail(), true, true, true, account.getAudit().isEnabled(), authorities);
    }

}
