package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonRequestDtoTwo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;

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
class PersonValidationServiceUnitTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    IdentityVerificationService identityService;

    @InjectMocks
    PersonValidationService personValidationService;

    @Spy
    @InjectMocks
    PersonValidationService spyPersonValidationService;

    @Mock
    Environment env;

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
            doReturn("phoneNumber env").when(env).getProperty("person.validation.UniquePhoneNumber");
            personRequestDto.setPhoneNumber("2222");
            
            Optional<FieldInfoError> fieldOptional; 

            fieldOptional = spyPersonValidationService.validateUniquePhoneNumber(2L, personRequestDto);

            assertThat(fieldOptional).isNotEmpty();

            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("phoneNumber");
            assertThat(field.getValue()).isEqualTo("2222");
            assertThat(field.getOwnerClass()).isEqualTo(PersonRequestDto.class);
            assertThat(field.getType()).isEqualTo(String.class);
            assertThat(field.getErrorMessage()).isEqualTo("phoneNumber env");

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


        @BeforeEach
        void setUp(){
            doAnswer(invo ->{
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(spyPersonValidationService).isPersonRegistered(anyLong());
        }

        @Test
        void returnsOptionalFieldInfoError() {
            doReturn("registered env").when(env).getProperty("person.validation.PersonRegistered");
            Optional<FieldInfoError> fieldOptional;
            fieldOptional = spyPersonValidationService.validatePersonRegistered(2L);

            assertThat(fieldOptional).isNotEmpty();

            field = fieldOptional.orElseThrow();

            assertThat(field.getName()).isEqualTo("personId");
            assertThat(field.getErrorMessage()).isEqualTo("registered env");
            assertThat(field.getType()).isEqualTo(Long.class);
            assertThat(field.getValue()).isEqualTo(2L);

            verify(spyPersonValidationService, times(1)).isPersonRegistered(anyLong());
        }

        @Test
        void returnsEmptyOptionalFieldInfoError() {
            Optional<FieldInfoError> fieldOptional;
            fieldOptional = spyPersonValidationService.validatePersonRegistered(1L);
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
    

