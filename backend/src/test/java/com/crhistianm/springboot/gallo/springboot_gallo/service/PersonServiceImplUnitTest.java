package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createPersonOneDto;
import static org.mockito.Mockito.*;

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
    class registerModuleTest{

        @Test
        void testSave(){
            //Return an instance for the mapper
            when(personRepository.save(any(Person.class))).thenReturn(new PersonBuilder().build());
            personServiceImpl.save(createPersonOneDto().orElseThrow());
            verify(personRepository, times(1)).save(any(Person.class));
        }

    }

    @Test
    void testSave(){
        personServiceImpl.save(createPersonOneDto().orElseThrow());
        verify(personRepository, times(1)).save(any(Person.class));
    }
    
}
