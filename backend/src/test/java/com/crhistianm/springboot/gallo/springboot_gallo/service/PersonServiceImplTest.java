package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createPersonOneDto;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@SpringBootTest
public class PersonServiceImplTest {

    @MockitoBean
    PersonRepository personRepository;

    @Autowired
    PersonService personService;

    @BeforeEach
    void setUp(){
        //Return an instance for the mapper
        when(personRepository.save(any(Person.class))).thenReturn(new PersonBuilder().build());
    }

    @Test
    void testSave(){
        personService.save(createPersonOneDto().orElseThrow());
        verify(personRepository, times(1)).save(any(Person.class));
    }
    
}
