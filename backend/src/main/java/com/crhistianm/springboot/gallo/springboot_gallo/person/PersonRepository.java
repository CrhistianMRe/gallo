package com.crhistianm.springboot.gallo.springboot_gallo.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

interface PersonRepository extends CrudRepository <Person, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Page<Person> findBy(Pageable pageable);

}
