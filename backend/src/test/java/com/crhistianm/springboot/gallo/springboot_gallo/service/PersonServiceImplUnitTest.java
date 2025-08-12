package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonCreateDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityTwo;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;


@ExtendWith(MockitoExtension.class)
public class PersonServiceImplUnitTest {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    PersonServiceImpl personServiceImpl;

    @Nested
    class RegisterModuleTest{

        @Test
        void testSave(){
            //Return an instance for the mapper
            when(personRepository.save(any(Person.class))).thenReturn(new PersonBuilder().build());
            personServiceImpl.save(givenPersonCreateDtoOne().orElseThrow());
            verify(personRepository, times(1)).save(any(Person.class));
        }

    }

    @Nested
    class ValidationModuleTest{

        @Nested
        class IsPhoneNumberAvailableTest{

            @BeforeEach
            void setUp(){
                //return true just on that number
                when(personRepository.existsByPhoneNumber(anyString())).thenAnswer(invo -> {
                    return invo.getArgument(0).equals("1122334455");
                });
            }

            @Test
            void testNotAvailable(){
                assertFalse(personServiceImpl.isPhoneNumberAvailable("1122334455"));
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
            }

            @Test
            void testAvailable(){
                assertTrue(personServiceImpl.isPhoneNumberAvailable("4455667788"));
                verify(personRepository, times(1)).existsByPhoneNumber(anyString());
            }


        }

        @Nested
        class IsPersonRegisteredTest{

            @BeforeEach
            void setUp(){
                when(personRepository.findById(anyLong())).thenAnswer(invo ->{
                    Optional<Person> person = Optional.empty();
                    if(invo.getArgument(0, Long.class) == 1L){
                        person = Optional.of(PersonMapper.createToEntity(givenPersonCreateDtoOne().orElseThrow()));
                    }
                    return person; 
                });
            }

            @Test
            void testNotRegistered(){
                assertFalse(personServiceImpl.isPersonRegistered(2L));
                verify(personRepository, times(1)).findById(anyLong());
            }

            @Test
            void testRegistered(){
                assertTrue(personServiceImpl.isPersonRegistered(1L));
                verify(personRepository, times(1)).findById(anyLong());
            }
        }

    }

    @Nested
    class ViewModuleTest{

        @Test
        void testGetAll(){
            when(personRepository.findAll()).thenReturn(List.of(givenPersonEntityOne().orElseThrow(), givenPersonEntityTwo().orElseThrow()));

            List<PersonResponseDto> expectedList = List.of(PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow()), PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow()));
            assertEquals(expectedList, personServiceImpl.getAll());
            verify(personRepository, times(1)).findAll();
        }

    }

}

