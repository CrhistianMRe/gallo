package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

@Service
class PersonService {

    private final PersonRepository personRepository;

    private final PersonValidator personValidator;

    private final IdentityVerificationService identityService;

    PersonService(PersonRepository personRepository, PersonValidator personValidator, IdentityVerificationService identityService){
        this.personRepository = personRepository;
        this.personValidator = personValidator;
        this.identityService = identityService;
    }

    @Transactional
    PersonResponseDto save(PersonRequestDto personDto) {
        personValidator.validateRequest(null, personDto);
        Person person = PersonMapper.requestToEntity(personDto);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    PersonResponseDto update(Long id, PersonRequestDto personDto) {
        personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        personValidator.validateRequest(id, personDto);
        Person person = PersonMapper.requestToEntity(personDto);
        person.setId(id);
        return PersonMapper.entityToResponse(personRepository.save(person));
    }

    @Transactional
    PersonResponseDto delete(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        identityService.validateUserAllowance(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        personRepository.delete(person);
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    PersonResponseDto getById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        identityService.validateUserAllowance(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    List<PersonResponseDto> getAll() {
        List<PersonResponseDto> personList = personRepository.findAll().stream().map(p -> PersonMapper.entityToResponse(p)).collect(Collectors.toList());
        return personList;
    }

    
}
