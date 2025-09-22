package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
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
    public Optional<FieldInfoError> validateUniquePhoneNumber(Long pathPersonId, PersonRequestDto personDto){
        FieldInfoError field = null;
        if(!isPhoneNumberAvailable(pathPersonId, personDto.getPhoneNumber())){
            field = FieldInfoErrorMapper.classTargetToFieldInfo(personDto, "phoneNumber", "is already registered, user another one");
        }
        return Optional.ofNullable(field);
    }

    @Override
    public Optional<FieldInfoError> validatePersonRegistered(AccountRequestDto accountDto){
        FieldInfoError field = null;
        if(!isPersonRegistered(accountDto.getPersonId())) {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", "is not registered, register first!");
        }
        return Optional.ofNullable(field);
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
