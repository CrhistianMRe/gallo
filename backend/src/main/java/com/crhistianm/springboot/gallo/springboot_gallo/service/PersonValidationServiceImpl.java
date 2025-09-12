package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;

@Service
public class PersonValidationServiceImpl implements PersonValidationService{

    private final PersonRepository personRepository;

    private final IdentityVerificationService identityService;

    public PersonValidationServiceImpl(PersonRepository personRepository, IdentityVerificationService identityService) {
        this.personRepository = personRepository;
        this.identityService = identityService;
    }

    @Override
    public void validateRequest(Long pathPersonId, PersonRequestDto personDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        //If is null it is basically a create not update request so this validation is not activated
        if(pathPersonId != null && (!identityService.isAdminAuthority() && !identityService.isUserPersonEntityAllowed(pathPersonId))){
            fields.add(new FieldInfoErrorBuilder()
                    .name("path id")
                    .value(pathPersonId)
                    .type(pathPersonId.getClass())
                    .ownerClass(PersonRequestDto.class)
                    .errorMessage("is not allowed for this user!")
                    .build());
        }
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
