package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.PersonValidator;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    private final PersonValidator personValidator;

    private final IdentityVerificationService identityService;

    public PersonService(PersonRepository personRepository, PersonValidator personValidator, IdentityVerificationService identityService){
        this.personRepository = personRepository;
        this.personValidator = personValidator;
        this.identityService = identityService;
    }

    @Transactional
    public PersonResponseDto save(PersonRequestDto personDto) {
        personValidator.validateRequest(null, personDto);
        Person person = PersonMapper.requestToEntity(personDto);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    public PersonResponseDto update(Long id, PersonRequestDto personDto) {
        personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        personValidator.validateRequest(id, personDto);
        Person person = PersonMapper.requestToEntity(personDto);
        person.setId(id);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    public PersonResponseDto delete(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        identityService.validateUserAllowance(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        personRepository.delete(person);
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    public PersonResponseDto getById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        identityService.validateUserAllowance(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    public List<PersonResponseDto> getAll() {
        List<PersonResponseDto> personList = personRepository.findAll().stream().map(p -> PersonMapper.entityToResponse(p)).collect(Collectors.toList());
        return personList;
    }

    
}
