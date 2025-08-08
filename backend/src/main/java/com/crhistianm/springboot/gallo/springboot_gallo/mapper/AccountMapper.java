package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public class AccountMapper {

    public static Account createToEntity(AccountCreateDto accountDto){
        return new AccountBuilder().email(accountDto.getEmail()).password(accountDto.getPassword()).build();
    }

    public static AccountResponseDto entityToAdminResponse(Account account){
        AccountAdminResponseDto accountDto = new AccountAdminResponseDto();
        accountDto.setId(account.getId());
        accountDto.setEmail(account.getEmail());
        accountDto.setRoles(account.getRoles());
        accountDto.setPerson(account.getPerson());
        accountDto.setAudit(account.getAudit());
        return accountDto;
    }
    
    public static AccountResponseDto entityToResponse(Account account){
        AccountUserResponseDto accountDto = new AccountUserResponseDto();
        accountDto.setEmail(account.getEmail());
        return accountDto;
    }
}
