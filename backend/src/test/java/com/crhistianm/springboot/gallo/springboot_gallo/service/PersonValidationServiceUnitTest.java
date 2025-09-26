package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoTwo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
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
    class ValidateUniquePhoneNumberMethodTest{

        FieldInfoError field; 

        PersonRequestDto personRequestDto;

        @BeforeEach
        void setUp(){
            personRequestDto = new PersonRequestDto();
            doAnswer(invo ->{
                return invo.getArgument(1, String.class).equals("1111");
            }).when(spyPersonValidationService).isPhoneNumberAvailable(anyLong(), anyString());
        }

        @Test
        void returnsOptionalFieldInfoError() {
            personRequestDto.setPhoneNumber("2222");
            
            Optional<FieldInfoError> fieldOptional; 

            fieldOptional = spyPersonValidationService.validateUniquePhoneNumber(2L, personRequestDto);

            assertThat(fieldOptional).isNotEmpty();

            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("phoneNumber");
            assertThat(field.getValue()).isEqualTo("2222");
            assertThat(field.getOwnerClass()).isEqualTo(PersonRequestDto.class);
            assertThat(field.getType()).isEqualTo(String.class);
            assertThat(field.getErrorMessage()).isEqualTo("is already registered, user another one");

            verify(spyPersonValidationService).isPhoneNumberAvailable(anyLong(), anyString());
        }

        @Test
        void returnsEmptyOptionalFieldInfoError() {
            Optional<FieldInfoError> fieldOptional;
            personRequestDto.setPhoneNumber("1111");
            fieldOptional = spyPersonValidationService.validateUniquePhoneNumber(2L, personRequestDto);
            assertThat(fieldOptional).isEmpty();
        }
    }

    @Nested
    class ValidatePersonRegisteredMethodTest{

        FieldInfoError field;

        AccountRequestDto accountRequestDto;

        @BeforeEach
        void setUp(){
            accountRequestDto = new AccountRequestDto();
            doAnswer(invo ->{
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(spyPersonValidationService).isPersonRegistered(anyLong());
        }

        @Test
        void returnsOptionalFieldInfoError() {
            Optional<FieldInfoError> fieldOptional;
            accountRequestDto.setPersonId(2L);
            fieldOptional = spyPersonValidationService.validatePersonRegistered(accountRequestDto);

            assertThat(fieldOptional).isNotEmpty();

            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("personId");
            assertThat(field.getErrorMessage()).isEqualTo("is not registered, register first!");
            assertThat(field.getOwnerClass()).isEqualTo(AccountRequestDto.class);
            assertThat(field.getType()).isEqualTo(Long.class);
            assertThat(field.getValue()).isEqualTo(2L);

            verify(spyPersonValidationService, times(1)).isPersonRegistered(anyLong());
        }

        @Test
        void returnsEmptyOptionalFieldInfoError() {
            Optional<FieldInfoError> fieldOptional;
            accountRequestDto.setPersonId(1L);
            fieldOptional = spyPersonValidationService.validatePersonRegistered(accountRequestDto);
            assertThat(fieldOptional).isEmpty();
        }
    }

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
        
}
    

