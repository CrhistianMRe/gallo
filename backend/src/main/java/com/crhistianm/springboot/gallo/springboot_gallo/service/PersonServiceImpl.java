package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return !personRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonRegistered(Long personId) {
        Optional<Person> optionalPerson = personRepository.findById(personId);
        return optionalPerson.isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> listAll() {
        List<PersonResponseDto> personList = personRepository.findAll().stream().map(p -> PersonMapper.entityToResponse(p)).collect(Collectors.toList());
        return personList;
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto listById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return PersonMapper.entityToResponse(personOptional.orElseThrow());
    }
    
}
