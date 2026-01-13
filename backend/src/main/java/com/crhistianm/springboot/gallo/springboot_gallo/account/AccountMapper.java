package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class AccountMapper {

    static Account requestToEntity(AccountRequestDto accountDto){
        return new AccountBuilder().email(accountDto.getEmail()).password(accountDto.getPassword()).build();
    }

    static AccountResponseDto entityToAdminResponse(Account account){
        AccountAdminResponseDto accountDto = new AccountAdminResponseDto();
        //List<RoleResponseDto> rolesResponseDto = account.getRoles().stream().map(r -> new RoleResponseDto(r.getId(), r.getName())).collect(Collectors.toList());
        List<RoleResponseDto> rolesResponseDto = account.getRoles().stream().map(role -> {
            RoleResponseDto roleDto = new RoleResponseDto();
            roleDto.setId(role.getId());
            roleDto.setName(role.getName());
            if(role.getAccounts() != null){
                List<AccountAdminResponseDto> accountList = new ArrayList<>();
                //Just add id and email as is the only information needed
                for (Account iterate: role.getAccounts()) {
                    AccountAdminResponseDto accountAdminResponseDto = new AccountAdminResponseDto();
                    accountAdminResponseDto.setId(iterate.getId());
                    accountAdminResponseDto.setEmail(iterate.getEmail());
                    accountList.add(accountAdminResponseDto);
                }
                roleDto.setAccounts(accountList);
            }
            return roleDto;
        }).collect(Collectors.toList());
        accountDto.setId(account.getId());
        accountDto.setEmail(account.getEmail());
        accountDto.setRoles(rolesResponseDto);
        accountDto.setAudit(account.getAudit());
        accountDto.setPersonId(account.getPerson().getId());
        return accountDto;
    }
    
    static AccountResponseDto entityToResponse(Account account){
        AccountUserResponseDto accountDto = new AccountUserResponseDto();
        accountDto.setEmail(account.getEmail());
        return accountDto;
    }
}
