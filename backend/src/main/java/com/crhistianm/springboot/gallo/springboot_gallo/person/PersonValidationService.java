package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.account.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;

@Service
public class PersonValidationService {

    private final PersonRepository personRepository;

    private final Environment env;

    PersonValidationService(PersonRepository personRepository, Environment env) {
        this.personRepository = personRepository;
        this.env = env;
    }

    Optional<FieldInfoError> validateUniquePhoneNumber(Long pathPersonId, PersonRequestDto personDto){
        FieldInfoError field = null;
        if(!isPhoneNumberAvailable(pathPersonId, personDto.getPhoneNumber())){
            field = FieldInfoErrorMapper.classTargetToFieldInfo(personDto, "phoneNumber", env.getProperty("person.validation.UniquePhoneNumber"));
        }
        return Optional.ofNullable(field);
    }

    public Optional<FieldInfoError> validatePersonRegistered(AbstractAccountRequestDto accountDto){
        FieldInfoError field = null;
        if(!isPersonRegistered(accountDto.getPersonId())) {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, "personId", env.getProperty("person.validation.PersonRegistered"));
        }
        return Optional.ofNullable(field);
    }

    @Transactional(readOnly = true)
    boolean isPhoneNumberAvailable(Long pathPersonId, String phoneNumber) {
        if(pathPersonId != null && personRepository.findById(pathPersonId).orElseThrow().getPhoneNumber().equals(phoneNumber)) return true;
        return !personRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional(readOnly = true)
    boolean isPersonRegistered(Long personId) {
        Optional<Person> optionalPerson = personRepository.findById(personId);
        return optionalPerson.isPresent();
    }

}
