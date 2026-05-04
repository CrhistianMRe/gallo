package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
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
        identityService.validateUserAllowanceByPersonId(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        personRepository.delete(person);
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    PersonResponseDto getById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class));
        identityService.validateUserAllowanceByPersonId(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
        return PersonMapper.entityToResponse(person);
    }

    @Transactional(readOnly = true)
    PagedModel<PersonResponseDto> getBy(int page, int size) {
        Page<PersonResponseDto> personPage = personRepository.findBy(PageRequest.of(page, size))
            .map(PersonMapper::entityToResponse);

        return new PagedModel<PersonResponseDto>(personPage);
    }
    
}
