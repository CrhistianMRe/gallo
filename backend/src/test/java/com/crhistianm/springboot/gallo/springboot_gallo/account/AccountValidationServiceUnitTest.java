package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.getPersonInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenAccountEntityAdmin;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.person.PersonValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceUnitTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    IdentityVerificationService identityService;

    @Mock
    PersonValidationService personValidationService;

    @Mock
    Environment env;

    @InjectMocks
    AccountValidationService accountValidationService;

    @Spy
    @InjectMocks
    AccountValidationService spyAccountValidationService;

    @Nested
    class ValidatePersonAssignedMethodTest {

        FieldInfoError field;

        @BeforeEach
        void setUp(){
            doAnswer(invo -> {
                return !invo.getArgument(1, Long.class).equals(1L);
            }).when(spyAccountValidationService).isPersonIdAssigned(isNull(), anyLong());
        }

        @Test
        void returnsOptionalFieldInfoError(){
            doReturn("person env").when(env).getProperty("account.validation.PersonAssigned");

            String email = null;
            String password = null;
            Long personId = 2L;
            boolean admin = false;

            AccountRequestDto accountRequestDto = new AccountRequestDto(email, password, personId, admin);

            Optional<FieldInfoError> fieldOptional;

            fieldOptional = spyAccountValidationService.validatePersonAssigned(null, accountRequestDto);

            assertThat(fieldOptional).isNotEmpty();

            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("personId");
            assertThat(field.getType()).isEqualTo(Long.class);
            assertThat(field.getValue()).isEqualTo(2L);
            assertThat(field.getOwnerClass()).isEqualTo(AbstractAccountRequestDto.class);
            assertThat(field.getErrorMessage()).isEqualTo("person env");

            verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());

        }

        @Test
        void returnsEmptyOptionalFieldInfoError(){
            Optional<FieldInfoError> fieldOptional;
            String email = null;
            String password = null;
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto accountRequestDto = new AccountRequestDto(email, password, personId, admin);

            fieldOptional = spyAccountValidationService.validatePersonAssigned(null, accountRequestDto);
            assertThat(fieldOptional).isEmpty();
        }

    }

    @Nested
    class ValidateUniqueEmailMethodTest {

        FieldInfoError field;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                return invo.getArgument(1, String.class).equals("example@gmail.com");
            }).when(spyAccountValidationService).isEmailAvailable(anyLong(), anyString());
        }

        @Test
        void returnsOptionalFieldInfoError() {
            doReturn("email env").when(env).getProperty("account.validation.UniqueEmail");
            String email = "invalid@gmail.com";
            String password = null;
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto accountRequestDto = new AccountRequestDto(email, password, personId, admin);

            Optional<FieldInfoError> fieldOptional;
            fieldOptional = spyAccountValidationService.validateUniqueEmail(2L, accountRequestDto);

            assertThat(fieldOptional).isNotEmpty();
            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("email");
            assertThat(field.getValue()).isEqualTo("invalid@gmail.com");
            assertThat(field.getType()).isEqualTo(String.class);
            assertThat(field.getOwnerClass()).isEqualTo(AbstractAccountRequestDto.class);
            assertThat(field.getErrorMessage()).isEqualTo("email env");
            verify(spyAccountValidationService).isEmailAvailable(anyLong(), anyString());
        }

        @Test
        void returnsEmptyOptionalFieldInfoError() {
            String email = "example@gmail.com";
            String password = null;
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto accountRequestDto = new AccountRequestDto(email, password, personId, admin);

            Optional<FieldInfoError> fieldOptional;
            fieldOptional = spyAccountValidationService.validateUniqueEmail(2L, accountRequestDto);
            assertThat(fieldOptional).isEmpty();
        }

    }

    @Nested
    class ValidateAccountRegisteredMethodTest {

        Optional<FieldInfoError> expectedErrorOptional;

        Long accountId;

        @BeforeEach
        void setUp() {
            expectedErrorOptional = Optional.empty();
            accountId = null;

            doAnswer(invo -> {
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(spyAccountValidationService).isAccountRegistered(anyLong());

            lenient().doAnswer(invo -> invo.getArgument(0, String.class)).when(env).getProperty(anyString());

        }

        @Test
        void shouldReturnErrorOptional() {
            accountId = 2L;

            expectedErrorOptional = spyAccountValidationService.validateAccountRegistered(accountId);

            assertThat(expectedErrorOptional).isNotEmpty();

            FieldInfoError expectedError = expectedErrorOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage).isEqualTo("account.validation.AccountRegistered");
            assertThat(expectedError).extracting(FieldInfoError::getValue).isEqualTo(accountId);
            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("accountId");
            assertThat(expectedError).extracting(FieldInfoError::getType).isEqualTo(accountId.getClass());

            verify(spyAccountValidationService, times(1)).isAccountRegistered(accountId);
        }

        @Test
        void shouldReturnEmptyErrorOptional() {
            accountId = 1L;

            expectedErrorOptional = spyAccountValidationService.validateAccountRegistered(accountId);

            assertThat(expectedErrorOptional).isEmpty();

            verify(spyAccountValidationService, times(1)).isAccountRegistered(accountId);
        }

    }

    @Nested
    class SettleResponseTypeTest {

        @Test
        void returnsAccountAdminResponseDtoWhenLoggedInUserHasAdminAuthority() {
            when(identityService.isAdminAuthority()).thenReturn(true);
            assertTrue(accountValidationService.settleResponseType(givenAccountEntityAdmin().orElseThrow()) instanceof AccountAdminResponseDto);
        }

        @Test
        void returnsAccountResponseDtoWhenLoggedInUserOnlyHasUserAuthority() {
            when(identityService.isAdminAuthority()).thenReturn(false);
            assertTrue(accountValidationService.settleResponseType(givenAccountEntityAdmin().orElseThrow()) instanceof AccountUserResponseDto);
        }

    }


    @Nested
    class IsEmailAvailableTest {

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                return invo.getArgument(0, String.class).equals("exists@gmail.com");
            }).when(accountRepository).existsByEmail(anyString());

        }

        @Nested
        class CreateRequestTest {

            @Test
            void returnsFalseWhenRequestEmailExistsInDb(){
                assertThat(accountValidationService.isEmailAvailable(null, "doesnotexist@gmail.com")).isTrue();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(0)).findById(anyLong());
            }

            @Test
            void returnsTrueWhenRequestEmailDoesNotExistInDb() {
                assertThat(accountValidationService.isEmailAvailable(null, "exists@gmail.com")).isFalse();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(0)).findById(anyLong());
            }

        }

        @Nested
        class UpdateRequestTest {

            @BeforeEach
            void setUp(){
                lenient().doReturn(givenAccountEntityAdmin())
                .when(accountRepository).findById(anyLong());
            }

            @Test
            void returnsTrueWhenRequestEmailMatchesDbAccountEmail() {
                assertThat(accountValidationService.isEmailAvailable(1L, "admin@gmail.com")).isTrue();
                verify(accountRepository, times(0)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

            @Test
            void returnsTrueWhenRequestEmailMisMatchDbAccountEmailAndIsNotInDb() {
                assertThat(accountValidationService.isEmailAvailable(1L, "doesnotexists@gmail.com")).isTrue();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

            @Test
            void returnsFalseWhenRequestEmailMismatchDbAccountEmailAndIsInDb() {
                assertThat(accountValidationService.isEmailAvailable(1L, "exists@gmail.com")).isFalse();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

        }
    }


        @Nested
        class IsPersonIdAssignedTest {

            @BeforeEach
            void setUp(){
                lenient().doAnswer(invo ->{
                    Optional<Account> accountOptional = Optional.empty();
                    if(invo.getArgument(0, Long.class).equals(1L)) accountOptional = givenAccountEntityAdmin();
                    return accountOptional;
                }).when(accountRepository).findById(anyLong());
            }

            @Nested
            class CreateRequestTest { 

                @Test
                void returnsTrueWhenAccountIsFoundByPersonId(){
                    assertThat(accountValidationService.isPersonIdAssigned(null, 1L));
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                    verify(accountRepository, times(0)).findById(anyLong());
                }
                
                @Test
                void returnsFalseWhenAccountIsNotFoundByPersonId(){
                    assertThat(accountValidationService.isPersonIdAssigned(null, 2L));
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                    verify(accountRepository, times(0)).findById(anyLong());
                }

            }

            
            @Nested
            class UpdateRequestTest {

                @BeforeEach
                void setUp(){
                    Person person = getPersonInstance();
                    person.setId(3L);
                    doReturn(Optional.of(new AccountBuilder().person(person).build()))
                        .when(accountRepository).findById(anyLong());
                }

                @Test
                void returnsTrueWhenPersonIdMatchesDbPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 3L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(0)).findAccountByPersonId(anyLong());
                }

                @Test
                void returnsTrueWhenPersonIdMismatchDbPersonIdAndAccountIsFoundByPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 1L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                }

                @Test
                void returnsFalseWhenPersonIdMismatchDbPersonIdAndAccountIsNotFoundByPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 2L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                }

            }

        }

    @Nested
    class IsAccountRegisteredTest {

        boolean result;

        Long accountId;

        @BeforeEach
        void setUp() {
            accountId = null;
            doAnswer(invo -> invo.getArgument(0, Long.class).equals(1L)).when(accountRepository).existsById(anyLong());
        }

        @Test
        void shouldReturnTrueWhenAccountIsRegistered() {
            accountId = 1L;

            result = accountValidationService.isAccountRegistered(accountId);

            assertThat(result).isTrue();

            verify(accountRepository, times(1)).existsById(accountId);
        }

        @Test
        void shouldReturnFalseWhenAccountIsNotRegistered() {
            accountId = 2L;

            result = accountValidationService.isAccountRegistered(accountId);

            assertThat(result).isFalse();

            verify(accountRepository, times(1)).existsById(accountId);
        }

    }


    
}

