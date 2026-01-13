package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface PersonRepository extends CrudRepository <Person, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    List<Person> findAll();
    
}
