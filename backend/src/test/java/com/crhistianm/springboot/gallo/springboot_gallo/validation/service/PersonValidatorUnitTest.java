package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonValidationService;

@ExtendWith(MockitoExtension.class)
public class PersonValidatorUnitTest {

    @Mock
    PersonValidationService personService;

    @Mock
    IdentityVerificationService identityService;

    @InjectMocks
    PersonValidator personValidator;

    PersonRequestDto personRequestDto;

    List<FieldInfoError> fields; 


    @Nested
    class ValidateRequestMethodTest {

        @BeforeEach
        void setUp() { 
            lenient().doReturn(Optional.empty()).when(identityService).validateUserAllowance(isNull());
            lenient().doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(1, PersonRequestDto.class).getPhoneNumber().equals("1111")) field = new FieldInfoErrorBuilder().name("phone").build();
                return Optional.ofNullable(field);
            }).when(personService).validateUniquePhoneNumber(isNull(), any(PersonRequestDto.class));
            personRequestDto = new PersonRequestDto();
            fields = new ArrayList<>();
        }

        @Test
        void shouldNotThrowExceptionWhenAnyAuthorityPhoneNumberIsAvailable(){
            personRequestDto.setPhoneNumber("1111");
            assertDoesNotThrow(() -> {
                personValidator.validateRequest(null, personRequestDto);
            });
            verify(identityService, times(1)).validateUserAllowance(isNull());
            verify(personService, times(1)).validateUniquePhoneNumber(isNull(), any(PersonRequestDto.class));
        }

        @Test
        void shouldThrowExceptionWhenAnyAuthorityPhoneNumberIsNotAvailable(){
            personRequestDto.setPhoneNumber("2222");
            List<FieldInfoError> fields = new ArrayList<>();
            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    personValidator.validateRequest(null, personRequestDto);
                }).actual().getFieldErrors();
            verify(identityService, times(1)).validateUserAllowance(isNull());
            verify(personService, times(1)).validateUniquePhoneNumber(isNull(), any(PersonRequestDto.class));

            assertThat(fields).hasSize(1);
            assertThat(fields.get(0).getName()).isEqualTo("phone");
        }

        @Nested
        class UpdateRequestTest {

            @BeforeEach
            void setUp() { 
                doAnswer(invo -> {
                    Long arg = invo.getArgument(0, Long.class);
                    FieldInfoError field = null;
                    if(!arg.equals(1L) && arg != null) field = new FieldInfoErrorBuilder().name("allowance").build();
                    return Optional.ofNullable(field);
                }).when(identityService).validateUserAllowance(anyLong());

                doAnswer(invo ->{
                    FieldInfoError field = null;
                    if(!invo.getArgument(1, PersonRequestDto.class).getPhoneNumber().equals("1111")) field = new FieldInfoErrorBuilder().name("phone").build();
                    return Optional.ofNullable(field);
                }).when(personService).validateUniquePhoneNumber(anyLong(), any(PersonRequestDto.class));
                personRequestDto = new PersonRequestDto();
                fields = new ArrayList<>();
            }

            @Test
            void shouldThrowExceptionWhenPhoneNumberIsNotAvailableButUserIsAllowed(){
                personRequestDto.setPhoneNumber("2222");
                fields = assertThatExceptionOfType(ValidationServiceException.class)
                    .isThrownBy(() ->{
                        personValidator.validateRequest(1L, personRequestDto);
                    }).actual().getFieldErrors();

                assertThat(fields).hasSize(1);
                assertThat(fields.get(0).getName()).isEqualTo("phone");
            }

            @Test
            void shouldThrowExceptionWhenUserIsNotAllowed(){
                personRequestDto.setPhoneNumber("1111");
                fields = assertThatExceptionOfType(ValidationServiceException.class)
                    .isThrownBy(() ->{
                        personValidator.validateRequest(2L, personRequestDto);
                    }).actual().getFieldErrors();
                assertThat(fields).hasSize(1);
                assertThat(fields.get(0).getName()).isEqualTo("allowance");
            }

            @Test
            void shouldThrowExceptionWithTwoErrorsWhenAllConditionsAreMet(){
                personRequestDto.setPhoneNumber("2222");
                fields = assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(() -> {
                    personValidator.validateRequest(2L, personRequestDto);
                }).actual().getFieldErrors();
                verify(identityService, times(1)).validateUserAllowance(anyLong());
                verify(personService, times(1)).validateUniquePhoneNumber(anyLong(), any(PersonRequestDto.class));

                Optional<FieldInfoError> fieldPhone = fields.stream().filter(field -> field.getName().equals("phone")).findFirst();
                Optional<FieldInfoError> fieldAllowance = fields.stream().filter(field -> field.getName().equals("allowance")).findFirst();

                assertThat(fields).hasSize(2);
                assertThat(fieldPhone).isNotEmpty();
                assertThat(fieldAllowance).isNotEmpty();
            }

            @Test
            void shouldNotThrowExceptionWhenAllConditionsAreValid(){
                personRequestDto.setPhoneNumber("1111");
                assertDoesNotThrow(() ->{
                    personValidator.validateRequest(1L, personRequestDto);
                });
                verify(identityService, times(1)).validateUserAllowance(anyLong());
                verify(personService, times(1)).validateUniquePhoneNumber(anyLong(), any(PersonRequestDto.class));
            }
            

        }


    }   
}
    



