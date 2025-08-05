package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createAccountAdmin;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createAccountUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;

@ExtendWith(MockitoExtension.class)
public class AccountUserDetailsServiceUnitTest {


    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountUserDetailsService service;

    @BeforeEach
    void setUp(){
        when(accountRepository.findByEmailWithRoles(anyString())).thenAnswer(invo -> {
            Optional<Account> accountOptional = Optional.empty();
            if(invo.getArgument(0).equals("admin@gmail.com")) accountOptional = createAccountAdmin();
            if(invo.getArgument(0).equals("user@gmail.com")) accountOptional = createAccountUser();
            return accountOptional;
        });
    }

    @Test
    void testLoadByUsernameAdmin(){
        CustomAccountUserDetails  customAccount = new CustomAccountUserDetails(
                "12345", 
                "admin@gmail.com", 
                "admin@gmail.com", 
                true, 
                true, 
                true, 
                true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")));
        CustomAccountUserDetails testAccount = (CustomAccountUserDetails) service.loadUserByUsername("admin@gmail.com");
        assertEquals(customAccount, testAccount);
        verify(accountRepository, times(1)).findByEmailWithRoles(anyString());
        assertThat(testAccount.getAuthorities()).hasSize(2);
    }

    @Test
    void testLoadByUsernameUser(){
        CustomAccountUserDetails customAccount = new CustomAccountUserDetails(
                "12345", 
                "user@gmail.com", 
                "user@gmail.com", 
                true, 
                true, 
                true, 
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        CustomAccountUserDetails testAccount = (CustomAccountUserDetails) service.loadUserByUsername("user@gmail.com");

        assertEquals(customAccount, testAccount);
        verify(accountRepository, times(1)).findByEmailWithRoles(anyString());
        assertThat(testAccount.getAuthorities()).hasSize(1);
    }

    @Test
    void testLoadByUsernameNotFound(){
        assertThrows(UsernameNotFoundException.class, () ->{
            service.loadUserByUsername("notexistsdb@gmail.com");
        });
    }

    
}
