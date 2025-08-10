package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;

public interface PersonRepository extends CrudRepository <Person, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    List<Person> findAll();
    
}
