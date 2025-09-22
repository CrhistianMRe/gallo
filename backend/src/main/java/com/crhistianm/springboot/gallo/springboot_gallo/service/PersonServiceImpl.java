package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.PersonValidator;

@Service
public class PersonServiceImpl implements PersonService{

    private final PersonRepository personRepository;

    private final PersonValidator personValidator;

    public PersonServiceImpl(PersonRepository personRepository, PersonValidator personValidator){
        this.personRepository = personRepository;
        this.personValidator = personValidator;
    }

    @Transactional
    @Override
    public PersonResponseDto save(PersonRequestDto personDto) {
        personValidator.validateRequest(null, personDto);
        Person person = PersonMapper.requestToEntity(personDto);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    @Override
    public PersonResponseDto update(Long id, PersonRequestDto personDto) {
        if(personRepository.findById(id).isEmpty()) throw new NotFoundException(Person.class);
        personValidator.validateRequest(id, personDto);
            Person person = PersonMapper.requestToEntity(personDto);
            person.setId(id);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    @Override
    public PersonResponseDto delete(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if(personOptional.isEmpty())throw new NotFoundException(Person.class);
            personRepository.delete(personOptional.orElseThrow());
        return PersonMapper.entityToResponse(personOptional.orElseThrow());
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto getById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if(personOptional.isEmpty()) throw new NotFoundException(Person.class);
        return PersonMapper.entityToResponse(personOptional.orElseThrow());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> getAll() {
        List<PersonResponseDto> personList = personRepository.findAll().stream().map(p -> PersonMapper.entityToResponse(p)).collect(Collectors.toList());
        return personList;
    }

    
}
