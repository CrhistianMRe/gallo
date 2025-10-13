package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@Service
public class PersonValidationServiceImpl implements PersonValidationService{

    private final PersonRepository personRepository;

    private final Environment env;

    public PersonValidationServiceImpl(PersonRepository personRepository, Environment env) {
        this.personRepository = personRepository;
        this.env = env;
    }

    @Override
    public Optional<FieldInfoError> validateUniquePhoneNumber(Long pathPersonId, PersonRequestDto personDto){
        FieldInfoError field = null;
        if(!isPhoneNumberAvailable(pathPersonId, personDto.getPhoneNumber())){
            field = FieldInfoErrorMapper.classTargetToFieldInfo(personDto, "phoneNumber", env.getProperty("person.validation.UniquePhoneNumber"));
        }
        return Optional.ofNullable(field);
    }

    @Override
    public Optional<FieldInfoError> validatePersonRegistered(AbstractAccountRequestDto accountDto){
        FieldInfoError field = null;
        if(!isPersonRegistered(accountDto.getPersonId())) {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", env.getProperty("person.validation.PersonRegistered"));
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
