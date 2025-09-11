package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@Service
public class PersonValidationServiceImpl implements PersonValidationService{

    private final PersonRepository personRepository;

    public PersonValidationServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void validateRequest(Long pathPersonId, PersonRequestDto personDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        if(!isPhoneNumberAvailable(pathPersonId, personDto.getPhoneNumber())){
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(personDto, "phoneNumber", "is already registered, user another one"));
        }
        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPhoneNumberAvailable(Long pathPersonId, String phoneNumber) {
        if(pathPersonId != null && personRepository.findById(pathPersonId).orElseThrow().getPhoneNumber().equals(phoneNumber)) return true;
        return !personRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isPersonRegistered(Long personId) {
        Optional<Person> optionalPerson = personRepository.findById(personId);
        return optionalPerson.isPresent();
    }

}
