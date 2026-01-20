package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.*;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
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
            new CustomAccountUserDetails(50L,"12345", "example@gmail.com", "example@gmail.com", true, true, true, true, null);
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
    class IsUserAllowedMethod{

        @BeforeEach
        void setUp(){
            setSampleAuth(null);
        }

        @Test
        void shouldReturnTrueWhenIsAdmin() {
            doReturn(true).when(spyServiceIdentity).isAdminAuthority();
            boolean result = spyServiceIdentity.isUserAllowed(100L);

            assertThat(result).isTrue();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
        }

        @Test
        void shouldReturnTrueWhenIsNotAdminAndIsAllowed() {
            doReturn(false).when(spyServiceIdentity).isAdminAuthority();

            boolean result = spyServiceIdentity.isUserAllowed(50L);
            assertThat(result).isTrue();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
        }

        @Test
        void shouldReturnFalseWhenIsNotAdminAndIsNotAllowed() {
            doReturn(false).when(spyServiceIdentity).isAdminAuthority();

            boolean result = spyServiceIdentity.isUserAllowed(100L);
            assertThat(result).isFalse();

            verify(spyServiceIdentity, times(1)).isAdminAuthority();
        }

    }

    @Nested
    class ValidateUserAllowanceByPersonIdMethod {

        Optional<FieldInfoError> fieldOptional;

        @BeforeEach
        void setUp() {
            fieldOptional = Optional.empty();
            doAnswer(invo -> {
                Long personId = invo.getArgument(0, Long.class);
                Account account = new Account();
                account.setId(personId);
                if(invo.getArgument(0, Long.class).equals(20L)) account = null;
                return Optional.ofNullable(account);
            }).when(accountRepository).findAccountByPersonId(anyLong());

            lenient().doAnswer(invo -> {
                return invo.getArgument(0, Long.class).equals(2L);
            }).when(spyServiceIdentity).isUserAllowed(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenPersonIdIsNotFound() {
            String errorMessage = assertThatExceptionOfType(NotFoundException.class).
                isThrownBy(() -> spyServiceIdentity.validateUserAllowanceByPersonId(20L))
                .actual()
                .getMessage();

            assertThat(errorMessage).isEqualTo("Account not found");


            verify(accountRepository).findAccountByPersonId(eq(20L));
            verify(spyServiceIdentity, times(0)).isUserAllowed(anyLong());
            verifyNoInteractions(environment);
        }


        @Test
        void shouldReturnEmptyOptionalWhenUserIsAllowed(){
            fieldOptional = spyServiceIdentity.validateUserAllowanceByPersonId(2L);

            assertThat(fieldOptional).isEmpty();

            verify(accountRepository).findAccountByPersonId(eq(2L));
            verify(spyServiceIdentity, times(1)).isUserAllowed(eq(2L));
            verifyNoInteractions(environment);
        }

        @Test
        void shouldReturnErrorOptionalWhenUserIsNotAllowed() {
            doAnswer(invo -> invo.getArgument(0, String.class)).when(environment).getProperty(anyString());

            fieldOptional = spyServiceIdentity.validateUserAllowanceByPersonId(1L);

            assertThat(fieldOptional).isNotEmpty();

            FieldInfoError error = fieldOptional.orElseThrow();


            assertThat(error).extracting(FieldInfoError::getName).isEqualTo("path id");
            assertThat(error).extracting(FieldInfoError::getErrorMessage).isEqualTo("identity.validation.UserAllowance");
            assertThat(error).extracting(FieldInfoError::getValue).isEqualTo(1L);
            assertThat(error).extracting(FieldInfoError::getType).isEqualTo(Long.class);


            verify(accountRepository).findAccountByPersonId(eq(1L));
            verify(spyServiceIdentity, times(1)).isUserAllowed(eq(1L));
            verify(environment, times(1)).getProperty(eq("identity.validation.UserAllowance"));
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


        Optional<FieldInfoError> fieldOptional;

        @BeforeEach
        void setUp() {
            fieldOptional = Optional.empty();
            doAnswer(invo -> {
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(spyServiceIdentity).isUserAllowed(anyLong());
        }

        @Test
        void shouldReturnEmptyOptionalWhenUserIsAllowed() {
            fieldOptional = spyServiceIdentity.validateAllowanceByAccountId(1L);

            assertThat(fieldOptional).isEmpty();

            verify(spyServiceIdentity).isUserAllowed(eq(1L));
            verifyNoInteractions(environment);
        }

        @Test
        void shouldReturnErrorOptionalWhenUserIsNotAllowed() {
            doAnswer(invo -> invo.getArgument(0, String.class)).when(environment).getProperty(anyString());

            fieldOptional = spyServiceIdentity.validateAllowanceByAccountId(2L);

            assertThat(fieldOptional).isNotEmpty();

            FieldInfoError field = fieldOptional.orElseThrow();

            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("path id");
            assertThat(field).extracting(FieldInfoError::getValue).isEqualTo(2L);
            assertThat(field).extracting(FieldInfoError::getType).isEqualTo(Long.class);
            assertThat(field).extracting(FieldInfoError::getErrorMessage).isEqualTo("identity.validation.UserAllowance");

            verify(spyServiceIdentity, times(1)).isUserAllowed(eq(2L));
            verify(environment, times(1)).getProperty(eq("identity.validation.UserAllowance"));
        }
    }

}
