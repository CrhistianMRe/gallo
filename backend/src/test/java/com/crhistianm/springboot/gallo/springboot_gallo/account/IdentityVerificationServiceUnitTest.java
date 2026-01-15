package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.*;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.security.CustomAccountUserDetails;

@ExtendWith(MockitoExtension.class)
class IdentityVerificationServiceUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Environment environment;

    @InjectMocks
    private IdentityVerificationService serviceIdentity;

    @Spy
    @InjectMocks
    private IdentityVerificationService spyServiceIdentity;
    
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

    @Nested
    class ValidateUserAllowanceMethod {

        Optional<FieldInfoError> fieldOptional;

        @BeforeEach
        void setUp() {
            fieldOptional = Optional.empty();
            lenient().doAnswer(invo -> {
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(spyServiceIdentity).isUserPersonEntityAllowed(anyLong());
            //Most common scenario
            lenient().doReturn(false).when(spyServiceIdentity).isAdminAuthority();
        }

        @Test
        void shouldReturnEmptyOptionalWhenIdIsNull() {
            fieldOptional = spyServiceIdentity.validateUserAllowance(null);

            assertThat(fieldOptional).isEmpty();

            verify(spyServiceIdentity, times(0)).isUserPersonEntityAllowed(anyLong());
            verify(spyServiceIdentity, times(0)).isAdminAuthority();
            verify(environment, times(0)).getProperty(anyString());
        }

        @Test
        void shouldReturnEmptyOptionalWhenAuthorityIsAdmin() {
            //Admin authority mock
            doReturn(true).when(spyServiceIdentity).isAdminAuthority();
            fieldOptional = spyServiceIdentity.validateUserAllowance(100L);

            assertThat(fieldOptional).isEmpty();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
            verify(spyServiceIdentity, times(0)).isUserPersonEntityAllowed(anyLong());
            verify(environment, times(0)).getProperty(anyString());
        }

        @Test
        void shouldReturnEmptyOptionalWhenAuthorityUserIsAllowed() {
            fieldOptional = spyServiceIdentity.validateUserAllowance(1L);

            assertThat(fieldOptional).isEmpty();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
            verify(spyServiceIdentity, times(1)).validateUserAllowance(eq(1L));
            verify(environment, times(0)).getProperty(anyString());
        }

        @Test
        void shouldReturnErrorOptionalWhenAuthorityUserIsNotAllowed() {
            fieldOptional = spyServiceIdentity.validateUserAllowance(2L);
            FieldInfoError field = fieldOptional.orElseThrow();

            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("path id");
            assertThat(field).extracting(FieldInfoError::getValue).isEqualTo(2L);
            assertThat(field).extracting(FieldInfoError::getType).isEqualTo(Long.class);


            verify(spyServiceIdentity, times(1)).isAdminAuthority();
            verify(spyServiceIdentity, times(1)).validateUserAllowance(eq(2L));
            verify(environment, times(1)).getProperty("identity.validation.UserAllowance");
        }

    }

    @Nested
    class ValidateAdminRequiredMethod {

        Optional<FieldInfoError> fieldOptional;


        @BeforeEach
        void setUp() {
            fieldOptional = Optional.empty();
        }

        @Test
        void shouldReturnErrorOptionalWhenAuthorityIsNotAdmin() {
            doReturn("errormessage").when(environment).getProperty(anyString());
            doReturn(false).when(spyServiceIdentity).isAdminAuthority();
            AccountRequestDto accountDto = new AccountRequestDto();
            accountDto.setEmail("test");
            fieldOptional = spyServiceIdentity.validateAdminRequired(accountDto, "email");

            assertThat(fieldOptional).isNotEmpty();

            FieldInfoError field = fieldOptional.orElseThrow();

            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("email");
            assertThat(field).extracting(FieldInfoError::getErrorMessage).isEqualTo("errormessage");
            assertThat(field).extracting(FieldInfoError::getValue).isEqualTo("test");
            assertThat(field).extracting(FieldInfoError::getOwnerClass).isEqualTo(AbstractAccountRequestDto.class);
            assertThat(field).extracting(FieldInfoError::getType).isEqualTo(String.class);

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
            verify(environment, times(1)).getProperty(anyString());
        }

        @Test
        void shouldReturnEmptyOptionalWhenAuthorityIsAdmin() {
            doReturn(true).when(spyServiceIdentity).isAdminAuthority();
            AccountUpdateRequestDto accountDto = new AccountUpdateRequestDto();
            accountDto.setEnabled(true);

            fieldOptional = spyServiceIdentity.validateAdminRequired(accountDto, "enabled");
            assertThat(fieldOptional).isEmpty();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
            verifyNoInteractions(environment);
        }


    }

    @Nested
    class ValidateAllowanceByAccountIdMethod {

        Long accountId;

        @BeforeEach
        void setUp() {

            accountId = null;

            doAnswer(invo -> {
                Account account = null;
                Long argId = invo.getArgument(0, Long.class);
                if(argId.equals(1L)) {
                    account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(1L);
                }
                if(argId.equals(2L)) {
                    account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(2L);
                }
                return Optional.ofNullable(account);
            }).when(accountRepository).findById(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError fieldError = null;
                if(invo.getArgument(0, Long.class).equals(2L))  fieldError = new FieldInfoErrorBuilder().name("error").build();
                return Optional.ofNullable(fieldError);
            }).when(spyServiceIdentity).validateUserAllowance(anyLong());
        }

        @Test
        void shouldReturnErrorOptionalWhenUserIsNotAllowed() {
            accountId = 2L;
            FieldInfoError fieldError = spyServiceIdentity.validateAllowanceByAccountId(accountId).orElseThrow();

            assertThat(fieldError).extracting(FieldInfoError::getName).isEqualTo("error");
            verify(accountRepository, times(1)).findById(accountId);
            verify(spyServiceIdentity).validateUserAllowance(eq(2L));
        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound() {
            accountId = 3L;

            String error = assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> spyServiceIdentity.validateAllowanceByAccountId(accountId))
                .actual().getMessage();

            assertThat(error).isEqualTo("Account not found");
            verify(accountRepository).findById(accountId);
            verify(spyServiceIdentity, times(0)).validateUserAllowance(anyLong());
        }

        @Test
        void shouldReturnEmptyOptionalWhenUserIsAllowed() {
            accountId = 1L;
            Optional<FieldInfoError> optionalError = spyServiceIdentity.validateAllowanceByAccountId(accountId);

            assertThat(optionalError).isEmpty();

            verify(accountRepository).findById(accountId);
            verify(spyServiceIdentity).validateUserAllowance(eq(1L));
        }

    }

}
