package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityTwo;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.PersonValidator;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplUnitTest {

    @Mock
    PersonValidator personValidator;

    @Mock
    PersonRepository personRepository;

    @Mock
    PersonValidationService personValidationService;

    @Mock
    IdentityVerificationService identityVerificationService;

    @InjectMocks
    PersonServiceImpl personServiceImpl;

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
            personServiceImpl.save(givenPersonRequestDtoOne().orElseThrow());

            verify(personRepository, times(1)).save(any(Person.class));
            verify(personValidator, times(1)).validateRequest(isNull(), any(PersonRequestDto.class));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            PersonRequestDto requestDto = givenPersonRequestDtoOne().orElseThrow();
            requestDto.setPhoneNumber("666");

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personServiceImpl.save(requestDto));

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
            assertEquals(expectedList, personServiceImpl.getAll());
            verify(personRepository, times(1)).findAll();
        }

        @Test
        void testGetById(){
            PersonResponseDto expectedPerson = PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow());

            assertEquals(expectedPerson, personServiceImpl.getById(1L));
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void testGetByIdEmpty(){
            assertThrows(NotFoundException.class, () -> {
                personServiceImpl.getById(2L);
            });
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenGetByIdUserAllowanceIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personServiceImpl.getById(120L)).actual().getFieldErrors().get(0);

            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("identity");
        }

    }

    @Nested
    class UpdateModuleTest {

        @BeforeEach
        void setUp(){
            when(personRepository.findById(anyLong())).thenAnswer(invo -> {
                if(invo.getArgument(0, Long.class) == 1L){
                    return Optional.of(new Person());
                }
                return Optional.empty();
            });
        }
        


        @Test
        void testUpdate(){
            PersonRequestDto requestDto = givenPersonRequestDtoOne().orElseThrow();

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

            PersonResponseDto actualResponse = personServiceImpl.update(1L, requestDto);

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
                personServiceImpl.update(2L, new PersonRequestDto());
            });
            verify(personRepository, times(1)).findById(anyLong());
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
            assertEquals(expectedResponse, personServiceImpl.delete(1L));
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void testDeleteEmpty(){
            assertThrows(NotFoundException.class, () ->{
                personServiceImpl.delete(2L);
            });
            verify(personRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenDeleteUserAllowanceIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> personServiceImpl.getById(120L)).actual().getFieldErrors().get(0);

            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("identity");
        }

    }

}

