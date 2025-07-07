package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testSave(){
        Person person = new Person("Crhistian", "Mendez", "22211122", LocalDate.now(), "M", 1.52, 60.0,null);
        personRepository.save(person);

        assertTrue(personRepository.findById(1L).isPresent());
        assertEquals(person, personRepository.findById(1L).orElseThrow());
    }
    
}
