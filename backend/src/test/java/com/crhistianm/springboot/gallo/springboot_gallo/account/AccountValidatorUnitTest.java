package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.person.PersonValidationService;


@ExtendWith(MockitoExtension.class)
public class AccountValidatorUnitTest {
    
    @Mock
    PersonValidationService personService; 

    @Mock
    AccountValidationService accountService;

    @Mock
    IdentityVerificationService identityService;

    @Mock
    RoleValidationService roleService;

    @InjectMocks
    AccountValidator accountValidator;

    @Nested
    class ValidateRequestMethodTest {

        AccountRequestDto accountRequestDto;

        List<FieldInfoError> fields;

        @BeforeEach
        void setUp(){
            accountRequestDto = new AccountRequestDto();
            fields = new ArrayList<>();

            lenient().doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(0, Long.class).equals(1L)){
                    field = new FieldInfoErrorBuilder().name("registered").build();
                }
                return Optional.ofNullable(field);
            }).when(personService).validatePersonRegistered(anyLong());

            lenient().doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(1, AccountRequestDto.class).getPersonId().equals(1L)){
                    field = new FieldInfoErrorBuilder().name("assigned").build();
                }
                return Optional.ofNullable(field);
            }).when(accountService).validatePersonAssigned(isNull(), any(AccountRequestDto.class));

            doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(1, AccountRequestDto.class).getEmail().equals("example@gmail.com")){
                    field = new FieldInfoErrorBuilder().name("email").build();
                }
                return Optional.ofNullable(field);
            }).when(accountService).validateUniqueEmail(isNull(), any(AccountRequestDto.class));
            
             lenient().doAnswer(invo ->{
                 FieldInfoError field = null;
                 AccountRequestDto dto = invo.getArgument(0, AccountRequestDto.class);
                 if(dto.getPassword() != null && dto.getPassword().contains("adminerror")){
                     field = new FieldInfoErrorBuilder().name("isadmin").build();
                 }
                 return Optional.ofNullable(field);
             }).when(identityService).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

        @AfterEach
        void verifyMethodValidation(){
            verify(personService, times(1)).validatePersonRegistered(anyLong());
            verify(accountService, times(1)).validatePersonAssigned(isNull(), any(AccountRequestDto.class));
            verify(accountService, times(1)).validateUniqueEmail(isNull(), any(AccountRequestDto.class));
        }

        @Test
        void shouldNotThrowExceptionWhenAllConditionsAreValid() {
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setAdmin(true);

            assertDoesNotThrow(() -> {
                accountValidator.validateRequest(accountRequestDto);
            });
            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

        @Test
        void shouldThrowExceptionWith4ErrorsWhenAllConditionsAreMet(){
            accountRequestDto.setEmail("invalid@gmail.com");
            accountRequestDto.setPassword("adminerror");
            accountRequestDto.setPersonId(2L);
            accountRequestDto.setAdmin(true);
            
            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldRegistered = fields.stream().filter(f -> f.getName().equals("registered")).findFirst();
            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(f -> f.getName().equals("assigned")).findFirst();
            Optional<FieldInfoError> fieldEmail = fields.stream().filter(f -> f.getName().equals("email")).findFirst();
            Optional<FieldInfoError> fieldIsAdmin = fields.stream().filter(f -> f.getName().equals("isadmin")).findFirst();

            assertThat(fields).hasSize(4);

            assertThat(fieldRegistered).isNotEmpty();
            assertThat(fieldAssigned).isNotEmpty();
            assertThat(fieldEmail).isNotEmpty();
            assertThat(fieldIsAdmin).isNotEmpty();

            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

        @Test
        void shouldThrowExceptionWithOnlyPersonNotRegisteredError() {
            doReturn(Optional.of(new FieldInfoErrorBuilder().name("registered").build())).when(personService).validatePersonRegistered(anyLong());

            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPersonId(1L);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldRegistered = fields.stream().filter(f -> f.getName().equals("registered")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldRegistered).isNotEmpty();

            verify(identityService, times(0)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

        @Test
        void shouldThrowExceptionWithOnlyPersonAssignedError(){
            doReturn(Optional.of(new FieldInfoErrorBuilder().name("assigned").build())).when(accountService).validatePersonAssigned(isNull(), any(AccountRequestDto.class));

            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPersonId(1L);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(f -> f.getName().equals("assigned")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldAssigned).isNotEmpty();

            verify(identityService, times(0)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

        @Test
        void shouldThrowExceptionWithOnlyUniqueEmailError(){
            accountRequestDto.setEmail("invalid@gmail.com");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldEmail = fields.stream().filter(f->f.getName().equals("email")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldEmail).isNotEmpty();

            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }
        
        @Test
        void shouldThrowExceptionWithOnlyAdminRequiredError(){
            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPassword("adminerror");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldIsAdmin = fields.stream().filter(f->f.getName().equals("isadmin")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldIsAdmin).isNotEmpty();

            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyString());
        }

    }


    

    @Nested
    class ValidateUpdateRequestMethodTest {
        
        AccountUpdateRequestDto accountRequestDto;

        String errorsString;

        List<FieldInfoError> fields;

        private Optional<FieldInfoError> createField(String name){
            return Optional.of(new FieldInfoErrorBuilder().name(name).build());
        }

        @BeforeEach
        void setUp() {
            accountRequestDto = new AccountUpdateRequestDto();
            errorsString = "None";

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                if(!invo.getArgument(0, Long.class).equals(1L)) field = createField("userAllowance");
                return field;
            }).when(identityService).validateUserAllowanceByPersonId(anyLong());

            //For dto arguements email field will be used
            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                if(invo.getArgument(0, Long.class).equals(20L)){
                    field = createField("registered");
                }
                return field;
            }).when(personService).validatePersonRegistered(anyLong());

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                Optional<String> argumentString = Optional.ofNullable(invo.getArgument(1, AccountUpdateRequestDto.class).getPassword());
                if(argumentString.isPresent() && argumentString.orElseThrow().contains("assigned")) {
                    field = createField("assigned");
                }
                return field;
            }).when(accountService).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                Optional<String> argumentString = Optional.ofNullable(invo.getArgument(0, AccountUpdateRequestDto.class).getPassword());
                if(argumentString.isPresent() && argumentString.orElseThrow().contains("adminPersonId")){
                    field = createField("adminPersonId");
                };
                return field;
            }).when(identityService).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                Optional<String> argumentString = Optional.ofNullable(invo.getArgument(1, AccountUpdateRequestDto.class).getPassword());
                if(argumentString.isPresent() && argumentString.orElseThrow().contains("uniqueEmail")){
                    field = createField("uniqueEmail");
                }
                return field;
            }).when(accountService).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                Optional<String> argumentString = Optional.ofNullable(invo.getArgument(0, AccountUpdateRequestDto.class).getPassword());
                if(argumentString.isPresent() && argumentString.orElseThrow().contains("adminEnabled")){
                    field = createField("adminEnabled");
                };
                return field;
            }).when(identityService).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));

            lenient().doAnswer(invo -> {
                Optional<FieldInfoError> field = Optional.empty();
                Optional<String> argumentString = Optional.ofNullable(invo.getArgument(0, AccountUpdateRequestDto.class).getPassword());
                if(argumentString.isPresent() && argumentString.orElseThrow().contains("adminRole")){
                    field = createField("adminRole");
                }
                return field;
            }).when(identityService).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));

            lenient().doAnswer(invo -> {
                List<FieldInfoError> fields = new ArrayList<>();
                if(invo.getArgument(0, RoleRequestDto.class).getId().equals(2L)) fields.add(createField("id").orElseThrow());
                if(invo.getArgument(0, RoleRequestDto.class).getName().equals("adderror")) fields.add(createField("name").orElseThrow());
                return fields;
            }).when(roleService).validateRoleExists(any(RoleRequestDto.class));

        }

        @Nested
        class FieldConditionTest {

            @AfterEach
            void verifyMethodValidation() {
                verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());
            }

            @Test
            void shouldOnlyRunPersonIdFieldValidation() {
                accountRequestDto.setPersonId(2L);
                accountValidator.validateUpdateRequest(10L, accountRequestDto, 1L);

                verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
                verify(accountService, times(1)).validatePersonAssigned(eq(10L), eq(accountRequestDto));
                verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));

            }

            @Test
            void shouldOnlyRunEmailFieldValidation() {
                accountRequestDto.setEmail("example");
                accountValidator.validateUpdateRequest(10L, accountRequestDto, 1L);

                verify(accountService, times(1)).validateUniqueEmail(eq(10L), eq(accountRequestDto));

            }

            @Test
            void shouldOnlyRunEnabledFieldValidation() {
                accountRequestDto.setEnabled(true);
                accountValidator.validateUpdateRequest(10L, accountRequestDto, 1L);

                verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("enabled"));

            }

            @Test
            void shouldOnlyRunRolesFieldValidation() {
                accountRequestDto.setRoles(List.of(new RoleRequestDto(1L, "name")));
                accountValidator.validateUpdateRequest(10L, accountRequestDto, 1L);

                verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
                verify(roleService, times(1)).validateRoleExists(any(RoleRequestDto.class));

            }


        }

        //registered and assigend cannot happen at the same request
        @Test
        void shouldThrowExceptionWithMaximumPossibleErrorsWith1Role() {
            errorsString = "assigned, adminPersonId, uniqueEmail, adminEnabled, adminRole";
            accountRequestDto.setPersonId(30L);
            accountRequestDto.setRoles(List.of(new RoleRequestDto(2L, "adderror")));
            accountRequestDto.setPassword(errorsString);
            accountRequestDto.setEmail("email");
            accountRequestDto.setEnabled(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateUpdateRequest(123123123L, accountRequestDto, 2L);
                }).actual().getFieldErrors();

            assertThat(fields).hasSize(8);

            Optional<FieldInfoError> fieldUserAllowance = fields.stream().filter(error -> error.getName().equals("userAllowance")).findFirst();
            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(error -> error.getName().equals("assigned")).findFirst();
            Optional<FieldInfoError> fieldAdminPersonId = fields.stream().filter(error -> error.getName().equals("adminPersonId")).findFirst();
            Optional<FieldInfoError> fieldUniqueEmail = fields.stream().filter(error -> error.getName().equals("uniqueEmail")).findFirst();
            Optional<FieldInfoError> fieldAdminEnabled = fields.stream().filter(error -> error.getName().equals("adminEnabled")).findFirst();
            Optional<FieldInfoError> fieldAdminRole = fields.stream().filter(error -> error.getName().equals("adminRole")).findFirst();
            Optional<FieldInfoError> fieldRoleId = fields.stream().filter(error -> error.getName().equals("id")).findFirst();
            Optional<FieldInfoError> fieldRoleName = fields.stream().filter(error -> error.getName().equals("name")).findFirst();

            assertThat(fieldUserAllowance).isNotEmpty();
            assertThat(fieldAssigned).isNotEmpty();
            assertThat(fieldAdminPersonId).isNotEmpty();
            assertThat(fieldUniqueEmail).isNotEmpty();
            assertThat(fieldAdminEnabled).isNotEmpty();
            assertThat(fieldAdminRole).isNotEmpty();
            assertThat(fieldRoleId).isNotEmpty();
            assertThat(fieldRoleName).isNotEmpty();

            verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(2L));
            verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
            verify(accountService, times(1)).validatePersonAssigned(eq(123123123L), eq(accountRequestDto));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
            verify(accountService, times(1)).validateUniqueEmail(eq(123123123L), eq(accountRequestDto));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("enabled"));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
            verify(roleService, times(1)).validateRoleExists(any(RoleRequestDto.class));

        }

        @Test
        void shouldThrowExceptionWithMaximumPossibleErrorsWithMultipleRole() {
            errorsString = "assigned, adminPersonId, uniqueEmail, adminEnabled, adminRole";
            accountRequestDto.setPersonId(3L);
            List<RoleRequestDto> roleList = new ArrayList<>();
            for(int i = 0; i < 10; i++) roleList.add(new RoleRequestDto((2L), "adderror"));
            accountRequestDto.setPassword(errorsString);
            accountRequestDto.setEmail("email");
            accountRequestDto.setEnabled(true);
            accountRequestDto.setRoles(roleList);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateUpdateRequest(123123123L, accountRequestDto, 2L);
                }).actual().getFieldErrors();

            assertThat(fields).hasSize(26);

            Optional<FieldInfoError> fieldUserAllowance = fields.stream().filter(error -> error.getName().equals("userAllowance")).findFirst();
            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(error -> error.getName().equals("assigned")).findFirst();
            Optional<FieldInfoError> fieldAdminPersonId = fields.stream().filter(error -> error.getName().equals("adminPersonId")).findFirst();
            Optional<FieldInfoError> fieldUniqueEmail = fields.stream().filter(error -> error.getName().equals("uniqueEmail")).findFirst();
            Optional<FieldInfoError> fieldAdminEnabled = fields.stream().filter(error -> error.getName().equals("adminEnabled")).findFirst();
            Optional<FieldInfoError> fieldAdminRole = fields.stream().filter(error -> error.getName().equals("adminRole")).findFirst();

            List<FieldInfoError> fieldRoleId = fields.stream().filter(error -> error.getName().equals("id")).toList();
            List<FieldInfoError> fieldRoleName = fields.stream().filter(error -> error.getName().equals("name")).toList();

            assertThat(fieldUserAllowance).isNotEmpty();
            assertThat(fieldAssigned).isNotEmpty();
            assertThat(fieldAdminPersonId).isNotEmpty();
            assertThat(fieldUniqueEmail).isNotEmpty();
            assertThat(fieldAdminEnabled).isNotEmpty();
            assertThat(fieldAdminRole).isNotEmpty();

            assertThat(fieldRoleId).hasSize(10);
            assertThat(fieldRoleName).hasSize(10);

            verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(2L));
            verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
            verify(accountService, times(1)).validatePersonAssigned(eq(123123123L), eq(accountRequestDto));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
            verify(accountService, times(1)).validateUniqueEmail(eq(123123123L), eq(accountRequestDto));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("enabled"));
            verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
            verify(roleService, times(10)).validateRoleExists(any(RoleRequestDto.class));

        }

        @Test
        void shouldNotThrowExceptionWhenUserAuthorityIsAllowed(){
            assertDoesNotThrow(() -> {
                accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
            });

            verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(1L));

            verify(personService, times(0)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
            verify(identityService, times(0)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
            verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
            verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
            verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
            verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
            verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));
        }

        @Test
        void shouldThrowExceptionWhenUserAuthorityIsNotAllowed() {
            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateUpdateRequest(3L, accountRequestDto, 2L);
                }).actual().getFieldErrors();

            assertThat(fields).hasSize(1);

            Optional<FieldInfoError> fieldUserAllowance = fields.stream().filter(error -> error.getName().equals("userAllowance")).findFirst();

            assertThat(fieldUserAllowance).isNotEmpty();

            verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(2L));

            verify(personService, times(0)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
            verify(identityService, times(0)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
            verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
            verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
            verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
            verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
            verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));

        }

        @Nested
        class SingleFieldTest {

            @Nested
            class PersonIdFieldTest{

                @Test
                void shouldThrowExceptionWhenPersonIsNotRegistered() {   
                    errorsString = "registered";
                    accountRequestDto.setPassword(errorsString);
                    accountRequestDto.setPersonId(20L);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldRegistered = fields.get(0);

                    assertThat(fieldRegistered.getName()).isEqualTo("registered");

                    verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(1L));
                    verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
                    verify(accountService, times(1)).validatePersonAssigned(eq(3L), eq(accountRequestDto));


                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                    verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));

                }


                @Test
                void shouldThrowExceptionWhenPersonIsAssigned() {
                    errorsString = "assigned";
                    accountRequestDto.setPersonId(2L);
                    accountRequestDto.setPassword(errorsString);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() ->{
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldAssigned = fields.get(0);

                    assertThat(fieldAssigned.getName()).isEqualTo("assigned");

                    verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(1L));
                    verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
                    verify(accountService, times(1)).validatePersonAssigned(eq(3L), eq(accountRequestDto));

                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                    verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));

                }

                @Test
                void shouldThrowExceptionWhenUserIsNotAdmin() {
                    errorsString = "adminPersonId";
                    accountRequestDto.setPersonId(2L);
                    accountRequestDto.setPassword(errorsString);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldAdminPersonId = fields.get(0);

                    assertThat(fieldAdminPersonId.getName()).isEqualTo("adminPersonId");

                    verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(1L));
                    verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
                    verify(accountService, times(1)).validatePersonAssigned(eq(3L), eq(accountRequestDto));

                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                    verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));
                }

                //tests seem repetitive but is just for document scenarios
                @Nested
                class NotThrownExceptionTest {

                    @AfterEach
                    void verifyMethodValidation() {
                        verify(identityService, times(1)).validateUserAllowanceByPersonId(eq(1L));
                        verify(personService, times(1)).validatePersonRegistered(eq(accountRequestDto.getPersonId()));
                        verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("personId"));
                        verify(accountService, times(1)).validatePersonAssigned(eq(3L), eq(accountRequestDto));

                        verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                        verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                        verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                        verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));
                    }


                    @Test
                    void shouldNotThrowExceptionWhenPersonIsRegistered() {
                        accountRequestDto.setPersonId(2L);

                        assertDoesNotThrow(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        });

                    }

                    @Test
                    void shouldNotThrowExceptionWhenPersonIsNotAssigned() {
                        accountRequestDto.setPersonId(2L);
                        accountRequestDto.setPassword(errorsString);

                        assertDoesNotThrow(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        });

                    }

                    @Test
                    void shouldNotThrowExceptionWhenUserIsAdmin() {
                        accountRequestDto.setPersonId(2L);
                        accountRequestDto.setPassword(errorsString);

                        assertDoesNotThrow(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        });

                    }

                }

            }

            @Nested
            class EmailFieldTest { 

                @BeforeEach
                void setUp(){
                    accountRequestDto.setEmail("email");
                }

                @AfterEach
                void verifyMethodValidation() {
                    verify(accountService, times(1)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(),  any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                    verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));
                }

                @Test
                void shouldThrowExceptionWhenEmailIsNotValid() {
                    accountRequestDto.setPassword("uniqueEmail");

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldUniqueEmail = fields.get(0);

                    assertThat(fieldUniqueEmail.getName()).isEqualTo("uniqueEmail");
                }

                @Test
                void shouldNotThrowExceptionWhenEmailIsValid() {
                    assertDoesNotThrow(() -> accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L));
                }

            }

            @Nested
            class EnabledFieldTest {

                @BeforeEach
                void setUp(){
                    accountRequestDto.setEnabled(true);
                }

                @AfterEach
                void verifyMethodValidation() { 
                    verify(identityService, times(1)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("roles"));
                    verify(roleService, times(0)).validateRoleExists(any(RoleRequestDto.class));

                }
                
                @Test
                void shouldThrowExceptionWhenUserIsNotAdmin() {
                    accountRequestDto.setPassword("adminEnabled");

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldAdminEnabled = fields.get(0);

                    assertThat(fieldAdminEnabled.getName()).isEqualTo("adminEnabled");

                }

                @Test
                void shouldNotThrowExceptionWhenUserIsAdmin() {
                    assertDoesNotThrow(() -> accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L));
                }

            }

            @Nested
            class RolesFieldTest{

                List<RoleRequestDto> roleList;

                @BeforeEach
                void setUp(){
                    roleList = new ArrayList<>();
                }

                @Test
                void shouldThrowExceptionWhenIdIsNotValid() {
                    roleList.add(new RoleRequestDto(2L, "name"));
                    accountRequestDto.setRoles(roleList);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldRole = fields.get(0);

                    assertThat(fieldRole.getName()).isEqualTo("id");

                    verify(roleService, times(1)).validateRoleExists(any(RoleRequestDto.class));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                }

                @Test
                void shouldThrowExceptionWhenNameIsNotValid() {
                    roleList.add(new RoleRequestDto(1L, "adderror"));
                    accountRequestDto.setRoles(roleList);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(1);

                    FieldInfoError fieldRole = fields.get(0);

                    assertThat(fieldRole.getName()).isEqualTo("name");

                    verify(roleService, times(1)).validateRoleExists(any(RoleRequestDto.class));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));

                }

                @Test
                void shouldThrowExceptionWhenMultipleRoleHaveAllFieldsInvalid() { 
                    for(int i = 0; i < 10; i++) roleList.add(new RoleRequestDto((2L), "adderror"));
                    accountRequestDto.setRoles(roleList);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            accountValidator.validateUpdateRequest(anyLong(), accountRequestDto, 1L);
                        }).actual().getFieldErrors();

                    assertThat(fields).hasSize(20);

                    List<FieldInfoError> idFields = fields.stream().filter(error -> error.getName().equals("id")).toList();

                    List<FieldInfoError> nameFields = fields.stream().filter(error -> error.getName().equals("name")).toList();

                    assertThat(idFields).hasSize(10);
                    assertThat(nameFields).hasSize(10);

                    verify(roleService, times(10)).validateRoleExists(any(RoleRequestDto.class));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));

                }

                @Test
                void shouldNotThrowExceptionWhenMultipleRoleHaveAllFieldsValid() {
                    for(int i = 0; i < 10; i++) roleList.add(new RoleRequestDto((1L), "name"));
                    accountRequestDto.setRoles(roleList);

                    assertDoesNotThrow(() -> {
                        accountValidator.validateUpdateRequest(3L, accountRequestDto, 1L);
                    });

                    verify(roleService, times(10)).validateRoleExists(any(RoleRequestDto.class));
                    verify(identityService, times(1)).validateAdminRequired(eq(accountRequestDto), eq("roles"));
                    verify(identityService, times(1)).validateUserAllowanceByPersonId(anyLong());

                    verify(personService, times(0)).validatePersonRegistered(anyLong());
                    verify(accountService, times(0)).validatePersonAssigned(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("personId"));
                    verify(accountService, times(0)).validateUniqueEmail(anyLong(), any(AccountUpdateRequestDto.class));
                    verify(identityService, times(0)).validateAdminRequired(any(AccountUpdateRequestDto.class), eq("enabled"));
                }

            }

        }


    }

    @Nested
    class ValidateByIdRequestMethodTest {

        @BeforeEach
        void setUp() {
            doAnswer(invo ->{
                FieldInfoError field = null;
                if(invo.getArgument(0, Long.class).equals(1L)) {
                    field = new FieldInfoError();
                    field.setName("exampleError");
                }
                return Optional.ofNullable(field);
            }).when(identityService).validateUserAllowanceByPersonId(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenUserIsNotAllowed() {
            FieldInfoError field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> accountValidator.validateByIdRequest(1L)).actual().getFieldErrors().get(0);

            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("exampleError");

            verify(identityService).validateUserAllowanceByPersonId(eq(1L));
        }

        @Test
        void shouldNotThrowExceptionWhenUserIsAllowed() {
            assertDoesNotThrow(() -> accountValidator.validateByIdRequest(2L));
            verify(identityService).validateUserAllowanceByPersonId(eq(2L));
        }

    }



}
