package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
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
    public PersonResponseDto save(PersonRequestDto personDto) {
        Person person = PersonMapper.requestToEntity(personDto);
        return PersonMapper.entityToResponse(personRepository.save(person));

    }

    @Transactional
    @Override
    public Optional<PersonResponseDto> update(Long id, PersonRequestDto personDto) {
        Optional<PersonResponseDto> responseDto = Optional.empty();
        if(personRepository.findById(id).isPresent()){
            Person person = PersonMapper.requestToEntity(personDto);
            person.setId(id);
            responseDto = Optional.of(PersonMapper.entityToResponse(personRepository.save(person)));
        }
        return responseDto;
    }

    @Transactional
    @Override
    public Optional<PersonResponseDto> delete(Long id) {
        Optional<PersonResponseDto> responseDto =  Optional.empty();
        Optional<Person> personOptional = personRepository.findById(id);
        if(personOptional.isPresent()){
            personRepository.delete(personOptional.orElseThrow());
            responseDto = Optional.of(PersonMapper.entityToResponse(personOptional.orElseThrow()));
        }
        return responseDto;
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
    public List<PersonResponseDto> getAll() {
        List<PersonResponseDto> personList = personRepository.findAll().stream().map(p -> PersonMapper.entityToResponse(p)).collect(Collectors.toList());
        return personList;
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto getById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return PersonMapper.entityToResponse(personOptional.orElseThrow());
    }
    
}
