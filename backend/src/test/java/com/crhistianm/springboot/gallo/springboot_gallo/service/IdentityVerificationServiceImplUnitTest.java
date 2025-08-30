package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;

@ExtendWith(MockitoExtension.class)
public class IdentityVerificationServiceImplUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private IdentityVerificationServiceImpl serviceIdentity;
    
    
    private void setSampleAuth(Collection<? extends GrantedAuthority> authorities){
        CustomAccountUserDetails userDetails = 
            new CustomAccountUserDetails("12345", "example@gmail.com", "example@gmail.com", true, true, true, true, null);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Nested
    class IsAdminAuthorityMethod{

        @Test
        void testFalse(){
            Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            setSampleAuth(authorities);
            assertFalse(serviceIdentity.isAdminAuthority());
        }

        @Test
        void testTrue(){
            Collection<? extends GrantedAuthority> authorities = List
                .of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")); 
            setSampleAuth(authorities);
            assertTrue(serviceIdentity.isAdminAuthority());
        }

    }

    @Nested
    class IsPersonEntityAllowedMethod {

        @BeforeEach
        void setUp(){
            Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            setSampleAuth(authorities);
            when(accountRepository.findAccountByPersonId(anyLong())).thenAnswer(invo ->{
                Optional<Account> accountDb = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) accountDb = Optional.of(new AccountBuilder().email("example@gmail.com").build());
                if(invo.getArgument(0, Long.class) == 2L) accountDb = Optional.of(new AccountBuilder().email("example2@gmail.com").build());
                return accountDb;
            });

        }

        @Test
        void testFalse() {
            assertFalse(serviceIdentity.isUserPersonEntityAllowed(2L));
            verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
        }

        @Test
        void testTrue() {
            assertTrue(serviceIdentity.isUserPersonEntityAllowed(1L));
            verify(accountRepository, times(1)).findAccountByPersonId(anyLong());

        }

        @Test
        void testNotFoundException(){
            assertThrowsExactly(NotFoundException.class, () ->serviceIdentity.isUserPersonEntityAllowed(3L));
            verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
        }



    }
}
