package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityTwo;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoTwo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.ThrowableAssertAlternative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonValidationServiceUnitTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    IdentityVerificationService identityService;

    @InjectMocks
    PersonValidationServiceImpl personValidationService;

    @Spy
    @InjectMocks
    PersonValidationServiceImpl spyPersonValidationService;

    @Nested
    class IsPersonRegistered {

        @BeforeEach
        void setUp(){
            doAnswer(invo ->{
                Optional<Person> personOptional = Optional.empty();
                if(invo.getArgument(0, Long.class).equals(1L)) personOptional = givenPersonEntityOne();
                return personOptional;
            }).when(personRepository).findById(anyLong());

        }

        @Test
        void returnsTrueWhenPersonIdIsFoundOnDb(){
            assertThat(personValidationService.isPersonRegistered(1L)).isTrue();
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void returnsFalseWhenPersonIdIsNotFoundOnDb(){
            assertThat(personValidationService.isPersonRegistered(2L)).isFalse();
            verify(personRepository, times(1)).findById(anyLong());
        }

    }

    @Nested
    class IsPhoneNumberAvailableMethodTest {

        @BeforeEach
        void setUp(){
            lenient().when(personRepository.findById(anyLong())).thenAnswer(invo -> {
                Optional<Person> personOptional = Optional.of(PersonMapper.requestToEntity(givenPersonRequestDtoTwo().orElseThrow()));
                if(invo.getArgument(0, Long.class).equals(1L)) personOptional = Optional.of(PersonMapper.requestToEntity(givenPersonRequestDtoOne().orElseThrow()));
                return personOptional;
            });
            lenient().when(personRepository.existsByPhoneNumber("1111")).thenReturn(true);
            lenient().when(personRepository.existsByPhoneNumber("2222")).thenReturn(false);
        }

        @Nested
        class UpdateRequestTest {

            @Test
            void returnsTrueWhenDbPhoneNumberEqualsRequestPhoneNumber() {
                assertThat(personValidationService.isPhoneNumberAvailable(1L, givenPersonRequestDtoOne().orElseThrow().getPhoneNumber()));
                verify(personRepository, times(0)).existsByPhoneNumber(anyString());
                verify(personRepository, times(1)).findById(anyLong());
            }

            @Test
            void returnsFalseWhenDbAndRequestPhoneNumbersAreNotEqualButExistsInDb() {
                assertThat(personValidationService.isPhoneNumberAvailable(2L, "1111")).isFalse();
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
                verify(personRepository, times(1)).findById(anyLong());
            }

            @Test
            void returnsTrueWhenDbAndRequestPhoneNumbersAreNotEqualButDoesNotExistInDb() {
                assertThat(personValidationService.isPhoneNumberAvailable(2L, "2222")).isTrue();
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
                verify(personRepository, times(1)).findById(anyLong());
            }
        }


        @Nested 
        class CreateRequestTest {

            @Test
            void returnsTrueWhenPhoneNumberDoesNotExistInDb() {
                assertThat(personValidationService.isPhoneNumberAvailable(null, "2222")).isTrue();
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
                verify(personRepository, times(0)).findById(anyLong());
            }

            @Test
            void returnsFalseWhenPhoneNumberDoesExistInDb() {
                assertThat(personValidationService.isPhoneNumberAvailable(null, "1111")).isFalse();
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
                verify(personRepository, times(0)).findById(anyLong());
            }

        }

    }

    @Nested
    class ValidateRequestMethodTest {

        PersonRequestDto personRequestDto;
        
        @BeforeEach
        void setUp(){
            personRequestDto = new PersonRequestDto();
            lenient().doAnswer(invo ->{
                return invo.getArgument(1, String.class).equals("1111");
            }).when(spyPersonValidationService).isPhoneNumberAvailable(eq(null), anyString());
        }

        @Test
        void shouldNotThrowExceptionWhenFieldInfoErrorListIsEmpty() {
            personRequestDto.setPhoneNumber("1111");
            assertDoesNotThrow(() ->{
                spyPersonValidationService.validateRequest(null, personRequestDto);
            });
            verify(identityService, times(0)).isUserPersonEntityAllowed(anyLong());
            verify(identityService, times(0)).isAdminAuthority();
        }

        @Test
        void shouldThrowExceptionWhenAnyAuthorityPhoneNumberIsNotAvailable() {
            List<FieldInfoError> expectedFieldErrors = new ArrayList<>();
            personRequestDto.setPhoneNumber("2222");

            ThrowableAssertAlternative<ValidationServiceException> alternative;
            alternative = assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(() -> {
                spyPersonValidationService.validateRequest(null, personRequestDto);
            });

            expectedFieldErrors = alternative.actual().getFieldErrors();

            assertThat(expectedFieldErrors).extracting(error -> error.getName()).containsOnlyOnce("phoneNumber");
            assertThat(expectedFieldErrors).extracting(error -> error.getErrorMessage()).containsOnlyOnce("is already registered, user another one");
            assertThat(expectedFieldErrors).hasSize(1);
            verify(identityService, times(0)).isUserPersonEntityAllowed(anyLong());
            verify(identityService, times(0)).isAdminAuthority();
        }

        @Test
        void shouldNotThrowExceptionWhenAnyAuthorityPhoneNumberIsAvailable() {
            personRequestDto.setPhoneNumber("1111");
            assertDoesNotThrow(()-> spyPersonValidationService.validateRequest(null, personRequestDto));
            verify(identityService, times(0)).isUserPersonEntityAllowed(anyLong());
            verify(identityService, times(0)).isAdminAuthority();
        }


        @Nested
        class UpdateRequestTest {

            @BeforeEach
            void setUp(){
                personRequestDto = new PersonRequestDto();
                doAnswer(invo ->{
                    return invo.getArgument(1, String.class).equals("1111");
                }).when(spyPersonValidationService).isPhoneNumberAvailable(anyLong(), anyString());

                lenient().when(identityService.isUserPersonEntityAllowed(anyLong())).thenAnswer(invo -> {
                    return invo.getArgument(0, Long.class).equals(1L);
                });
            }

            @Test
            void shouldThrowExceptionWithTwoErrorsWhenAllConditionsAreMet() {
                when(identityService.isAdminAuthority()).thenReturn(false);

                List<FieldInfoError> expectedFieldErrors = new ArrayList<>();
                personRequestDto.setPhoneNumber("2222");

                expectedFieldErrors = assertThatExceptionOfType(ValidationServiceException.class)
                    .isThrownBy(()-> spyPersonValidationService.validateRequest(2L, personRequestDto)).actual().getFieldErrors();

                assertThat(expectedFieldErrors).hasSize(2);

                assertThat(expectedFieldErrors).extracting(FieldInfoError::getErrorMessage).containsOnlyOnce("is already registered, user another one");
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getErrorMessage).containsOnlyOnce("is not allowed for this user!");

                assertThat(expectedFieldErrors).extracting(FieldInfoError::getName).containsOnlyOnce("phoneNumber");
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getName).containsOnlyOnce("path id");

                assertThat(expectedFieldErrors).extracting(FieldInfoError::getValue).containsOnlyOnce("2222");
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getValue).containsOnlyOnce(2L);

                FieldInfoError phoneError = expectedFieldErrors.stream().filter(error -> error.getName().equals("phoneNumber")).findFirst().orElseThrow();
                FieldInfoError pathIdError = expectedFieldErrors.stream().filter(error -> error.getName().equals("path id")).findFirst().orElseThrow();

                assertThat(phoneError.getOwnerClass()).isEqualTo(PersonRequestDto.class);
                assertThat(pathIdError.getOwnerClass()).isEqualTo(PersonRequestDto.class);

                assertThat(phoneError.getType()).isEqualTo(String.class);
                assertThat(pathIdError.getType()).isEqualTo(Long.class);
            }

            @Test
            void shouldNotThrowExceptionWhenAllUserConditionsAreValid(){
                when(identityService.isAdminAuthority()).thenReturn(false);

                personRequestDto.setPhoneNumber("1111");

                assertDoesNotThrow(() -> spyPersonValidationService.validateRequest(1L, personRequestDto));
            }

            @Test
            void shouldThrowExceptionWhenUserIsNotAllowed() {
                when(identityService.isAdminAuthority()).thenReturn(false);
                personRequestDto.setPhoneNumber("1111");

                List<FieldInfoError> expectedFieldErrors = new ArrayList<>();

                expectedFieldErrors = assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(() -> {
                    spyPersonValidationService.validateRequest(2L, personRequestDto);
                }).actual().getFieldErrors();

                assertThat(expectedFieldErrors).hasSize(1);
                //I extracting everyithing as is given manually
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getErrorMessage).contains("is not allowed for this user!");
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getValue).contains(2L);
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getName).contains("path id");
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getOwnerClass).first().isEqualTo(PersonRequestDto.class);
                assertThat(expectedFieldErrors).extracting(FieldInfoError::getType).first().isEqualTo(Long.class);
            }

            @Test
            void shouldNotThrowExceptionWhenAllAdminConditionsAreValid(){
                personRequestDto.setPhoneNumber("1111");
                when(identityService.isAdminAuthority()).thenReturn(true);
                assertDoesNotThrow(() ->{
                    spyPersonValidationService.validateRequest(2L, personRequestDto);

                });
                verify(identityService, times(0)).isUserPersonEntityAllowed(anyLong());
            }


        }
        
    }
    
}

