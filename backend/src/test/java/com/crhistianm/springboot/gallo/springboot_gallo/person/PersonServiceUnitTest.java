package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonEntityTwo;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;

@ExtendWith(MockitoExtension.class)
class PersonServiceUnitTest {

    @Mock
    PersonValidator personValidator;

    @Mock
    PersonRepository personRepository;

    @Mock
    PersonValidationService personValidationService;

    @Mock
    IdentityVerificationService identityVerificationService;

    @InjectMocks
    PersonService personService;

    @Nested
    class RegisterModuleTest{

        @BeforeEach
        void setUp() {

            lenient().doAnswer(invo -> {
                return invo.getArgument(0, Person.class);
            }).when(personRepository).save(any(Person.class));

            lenient().doAnswer(invo -> {
                if(invo.getArgument(1, PersonRequestDto.class).getPhoneNumber().equals("666")) {
                    throw new ValidationServiceException();
                }
                return null;
            }).when(personValidator).validateRequest(eq(null), any(PersonRequestDto.class));
        }


        @Test
        void testSave(){
            //Return an instance for the mapper
            personService.save(givenPersonRequestDtoOne().orElseThrow());

            verify(personRepository, times(1)).save(any(Person.class));
            verify(personValidator, times(1)).validateRequest(isNull(), any(PersonRequestDto.class));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            PersonRequestDto requestDto = givenPersonRequestDtoOne().orElseThrow();
            requestDto.setPhoneNumber("666");

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personService.save(requestDto));

            verify(personValidator, times(1)).validateRequest(eq(null), any(PersonRequestDto.class));
            verifyNoInteractions(personRepository);
        }

    }

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                FieldInfoError field = null;
                if(invo.getArgument(0, Long.class).equals(120L)) field = new FieldInfoErrorBuilder().name("identity").build();
                return Optional.ofNullable(field);
            }).when(identityVerificationService).validateUserAllowance(anyLong());
            lenient().when(personRepository.findById(anyLong())).thenAnswer(invo -> {
                Optional<Person> responseOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) responseOptional = givenPersonEntityOne();
                if(invo.getArgument(0, Long.class) == 120L) responseOptional = givenPersonEntityOne();
                return responseOptional;
            });
        }

        @Test
        void testGetAll(){
            when(personRepository.findAll()).thenReturn(List.of(givenPersonEntityOne().orElseThrow(), givenPersonEntityTwo().orElseThrow()));

            List<PersonResponseDto> expectedList = List.of(PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow()), PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow()));
            assertEquals(expectedList, personService.getAll());
            verify(personRepository, times(1)).findAll();
        }

        @Test
        void testGetById(){
            PersonResponseDto expectedPerson = PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow());

            assertEquals(expectedPerson, personService.getById(1L));
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void testGetByIdEmpty(){
            assertThrows(NotFoundException.class, () -> {
                personService.getById(2L);
            });
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenGetByIdUserAllowanceIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personService.getById(120L)).actual().getFieldErrors().get(0);

            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("identity");
        }

    }

    @Nested
    class UpdateModuleTest {

        PersonRequestDto requestDto;

        @BeforeEach
        void setUp(){
            requestDto = new PersonRequestDto();
            when(personRepository.findById(anyLong())).thenAnswer(invo -> {
                if(invo.getArgument(0, Long.class) == 1L){
                    return Optional.of(new Person());
                }
                return Optional.empty();
            });

            lenient().doAnswer(args -> {
                if(args.getArgument(1, PersonRequestDto.class).getFirstName().equals("error")) {
                    throw new ValidationServiceException();
                }
                return null;
            }).when(personValidator).validateRequest(anyLong(), any(PersonRequestDto.class));
        }
        


        @Test
        void testUpdate(){
            requestDto = givenPersonRequestDtoOne().orElseThrow();

            //Mock person updated already
            when(personRepository.save(any(Person.class))).thenAnswer(invo ->{
                Person personUpdated = invo.getArgument(0);
                personUpdated.setFirstName(requestDto.getFirstName());
                personUpdated.setLastName(requestDto.getLastName());
                personUpdated.setPhoneNumber(requestDto.getPhoneNumber());
                personUpdated.setBirthDate(requestDto.getBirthDate());
                personUpdated.setGender(requestDto.getGender());
                return personUpdated;
            });

            PersonResponseDto actualResponse = personService.update(1L, requestDto);

            Person personExpected = PersonMapper.requestToEntity(requestDto);
            personExpected.setId(1L);
            PersonResponseDto expectedResponse = PersonMapper.entityToResponse(personExpected);

            assertEquals(expectedResponse, actualResponse);

            verify(personRepository, times(1)).findById(anyLong());
            verify(personRepository, times(1)).save(any(Person.class));

        }

        @Test
        void testUpdateEmpty(){
            assertThrows(NotFoundException.class, () ->{
                personService.update(2L, new PersonRequestDto());
            });
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            requestDto = givenPersonRequestDtoOne().orElseThrow();
            requestDto.setFirstName("error");

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personService.update(1L, requestDto));
            verify(personRepository, times(1)).findById(eq(1L));
            verifyNoMoreInteractions(personRepository);
        }
        
    }

    @Nested
    class DeleteModuleTest {

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                FieldInfoError field = null;
                if(invo.getArgument(0, Long.class).equals(120L)) field = new FieldInfoErrorBuilder().name("identity").build();
                return Optional.ofNullable(field);
            }).when(identityVerificationService).validateUserAllowance(anyLong());
            when(personRepository.findById(anyLong())).thenAnswer(invo ->{
                Optional<Person> personDb = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) personDb = givenPersonEntityOne();
                if(invo.getArgument(0, Long.class) == 120L) personDb = givenPersonEntityOne();
                return personDb;
            });
        }

        @Test
        void testDelete(){
            PersonResponseDto expectedResponse = PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow());
            assertEquals(expectedResponse, personService.delete(1L));
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void testDeleteEmpty(){
            assertThrows(NotFoundException.class, () ->{
                personService.delete(2L);
            });
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenDeleteUserAllowanceIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personService.getById(120L)).actual().getFieldErrors().get(0);

            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("identity");
        }

    }

}

