package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public class AccountMapper {

    public static Account requestToEntity(AccountRequestDto accountDto){
        return new AccountBuilder().email(accountDto.getEmail()).password(accountDto.getPassword()).build();
    }

    public static AccountResponseDto entityToAdminResponse(Account account){
        //Set account to null as this response does not need account from person entity
        account.getPerson().setAccount(null);
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
        accountDto.setPerson(PersonMapper.entityToResponse(account.getPerson()));
        return accountDto;
    }
    
    public static AccountResponseDto entityToResponse(Account account){
        AccountUserResponseDto accountDto = new AccountUserResponseDto();
        accountDto.setEmail(account.getEmail());
        return accountDto;
    }
}
