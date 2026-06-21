package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import static com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheModule.ACCOUNT;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheHandlingUtils;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheResponseContext;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;

import jakarta.persistence.EntityManager;

@Service
class AccountService {

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountValidator accountValidator;

    private final AccountValidationService accountValidationService;

    private final EntityManager entityManager;

    private final CacheManager cacheManager;

    private final IdentityVerificationService identityVerificationService;

    AccountService
        (
         AccountRepository accountRepository,
         RoleRepository roleRepository,
         PasswordEncoder passwordEncoder,
         AccountValidator accountValidator,
         AccountValidationService accountValidationService,
         EntityManager entityManager,
         CacheManager cacheManager,
         IdentityVerificationService identityVerificationService 
        ){
            this.accountRepository = accountRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
            this.accountValidator = accountValidator;
            this.accountValidationService = accountValidationService;
            this.entityManager = entityManager;
            this.cacheManager = cacheManager;
            this.identityVerificationService = identityVerificationService;
        }

    @Transactional
    AccountResponseDto save(AccountRequestDto accountDto) {
        accountValidator.validateRequest(accountDto);

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        Account account = AccountMapper.requestToEntity(accountDto);

        account.setPerson(entityManager.getReference(Person.class, accountDto.getPersonId()));
        optionalRoleUser.ifPresent(account::addRole);

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (accountDto.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(account::addRole);
        }

        Account savedAccount = accountRepository.save(account);

        AccountResponseDto responseDto = accountValidationService.settleResponseType(savedAccount);

        //Cache only for user role
        if(!identityVerificationService.isAdminAuthority()) {
            cacheManager.getCache(ACCOUNT).put(savedAccount.getId(), responseDto);
        }

        return responseDto;
    }

    @Transactional
    AccountResponseDto update(Long id, AccountUpdateRequestDto accountDto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));

        accountValidator.validateUpdateRequest(id, accountDto);

        if(accountDto.getPersonId() != null) account.setPerson(entityManager.getReference(Person.class, accountDto.getPersonId()));
        if(accountDto.getEmail() != null) account.setEmail(accountDto.getEmail());
        if(accountDto.isEnabled() != null) account.getAudit().setEnabled(accountDto.isEnabled());
        if(accountDto.getPassword() != null) account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        if(!accountDto.getRoles().isEmpty()) account.setRoles(accountDto.getRoles().stream().map(role -> RoleMapper.requestToEntity(role)).collect(Collectors.toList()));

        AccountResponseDto responseDto = accountValidationService.settleResponseType(accountRepository.save(account));

        //Cache only for user role
        if(!identityVerificationService.isAdminAuthority()) {
            cacheManager.getCache(ACCOUNT).put(id, responseDto);
        }

        return responseDto;
    }

    @Transactional
    @CacheEvict(value = ACCOUNT, key = "#id")
    AccountResponseDto delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(Account.class));
        accountValidator.validateByIdRequest(id);
        accountRepository.delete(account);
        return accountValidationService.settleResponseType(account);
    }

    @Transactional(readOnly = true)
    AccountResponseDto getById(Long id) {
        if(!accountRepository.existsById(id)) throw new NotFoundException(Account.class);

        accountValidator.validateByIdRequest(id);

        //Cache only for user role
        if(!identityVerificationService.isAdminAuthority()) {

            CacheResponseContext<AccountUserResponseDto> cacheContext = CacheResponseContext.
                <AccountUserResponseDto>builder()
                .keyId(id)
                .cache(cacheManager.getCache(ACCOUNT))
                .responseType(AccountUserResponseDto.class)
                .onMissDo(() ->  {
                    Account account = accountRepository.findById(id).get();
                    return (AccountUserResponseDto) AccountMapper.entityToResponse(account);
                })
                .build();

            return CacheHandlingUtils.getOrCacheResponse(cacheContext);
        }

        Account account = accountRepository.findById(id).get();

        return AccountMapper.entityToAdminResponse(account);
    }

    @Transactional(readOnly = true)
    //This is not cached as it is only for admins
    PagedModel<AccountAdminResponseDto> getBy(int page, int size) {
        Page<AccountAdminResponseDto> accountPage = accountRepository.findBy(PageRequest.of(page, size))
            .map(account -> (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(account));
        return new PagedModel<AccountAdminResponseDto>(accountPage);
    }

}
