package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheHandlingUtils;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheResponseContext;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheModule.PERSON;

@Service
class PersonService {

    private final PersonRepository personRepository;

    private final PersonValidator personValidator;

    private final IdentityVerificationService identityService;

    private final CacheManager cacheManager;

    PersonService
        (
         PersonRepository personRepository,
         PersonValidator personValidator,
         IdentityVerificationService identityService,
         CacheManager cacheManager
        ){
            this.personRepository = personRepository;
            this.personValidator = personValidator;
            this.identityService = identityService;
            this.cacheManager = cacheManager;
        }

    @Transactional
    PersonResponseDto save(PersonRequestDto personDto) {
        personValidator.validateRequest(null, personDto);
        Person person = PersonMapper.requestToEntity(personDto);

        PersonResponseDto responseDto = PersonMapper.entityToResponse(personRepository.save(person));

        cacheManager.getCache(PERSON).put(responseDto.getId(), responseDto);

        return responseDto;
    }

    @Transactional
    PersonResponseDto update(Long id, PersonRequestDto personDto) {
        if(!personRepository.existsById(id)) throw new NotFoundException(Person.class);
        personValidator.validateRequest(id, personDto);
        Person person = PersonMapper.requestToEntity(personDto);

        person.setId(id);

        PersonResponseDto responseDto = PersonMapper.entityToResponse(personRepository.save(person));

        cacheManager.getCache(PERSON).put(responseDto.getId(), responseDto);
        return responseDto;
    }

    @Transactional
    @CacheEvict(value = PERSON, key = "#id")
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
        if(!personRepository.existsById(id)) throw new NotFoundException(Person.class);
        identityService.validateUserAllowanceByPersonId(id).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });

        CacheResponseContext<PersonResponseDto> cacheContext = CacheResponseContext
            //This reference is needed to specify the type to the generic builder
            .<PersonResponseDto>builder()
            .keyId(id)
            .cache(cacheManager.getCache(PERSON))
            .responseType(PersonResponseDto.class)
            .onMissDo(() -> {
                Person personEntity = personRepository.findById(id).get();
                return PersonMapper.entityToResponse(personEntity);
            })
            .build();

        return CacheHandlingUtils.getOrCacheResponse(cacheContext);
    }

    @Transactional(readOnly = true)
    //This is not cached as it is only for admins
    PagedModel<PersonResponseDto> getBy(int page, int size) {
        Page<PersonResponseDto> personPage = personRepository.findBy(PageRequest.of(page, size))
            .map(PersonMapper::entityToResponse);

        return new PagedModel<PersonResponseDto>(personPage);
    }
    
}
