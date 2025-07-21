package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@Service
public class PersonServiceImpl implements PersonService{

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @Transactional
    @Override
    public PersonResponseDto save(PersonCreateDto personDto) {
        Person person = PersonMapper.createToEntity(personDto);
        return PersonMapper.entityToResponse(personRepository.save(person));

    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return personRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonRegistered(Long personId) {
        Optional<Person> optionalPerson = personRepository.findById(personId);
        if(optionalPerson.isPresent()){
            return true;
        }
        return false;
    }
    
    
}
