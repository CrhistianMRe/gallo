package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testSave(){
        Person person = new PersonBuilder().firstName("Crhistian").lastName("Mendez").phoneNumber("22211122")
            .birthDate(LocalDate.now()).gender("M").height(1.52).weight(60.0).account(null).build();

        personRepository.save(person);

        assertTrue(personRepository.findById(1L).isPresent());
        assertEquals(person, personRepository.findById(1L).orElseThrow());
    }
    
}
