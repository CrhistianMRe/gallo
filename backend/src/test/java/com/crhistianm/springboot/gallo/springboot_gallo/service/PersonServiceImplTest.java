package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@SpringBootTest
public class PersonServiceImplTest {

    @MockitoBean
    PersonRepository personRepository;

    @Autowired
    PersonService personService;

    //Temporal test
    @Test
    void testSave(){
        personService.save(any());
        verify(personRepository, times(1)).save(any());
    }
    
}
