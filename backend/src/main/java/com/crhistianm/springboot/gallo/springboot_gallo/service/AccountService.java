package com.crhistianm.springboot.gallo.springboot_gallo.service;


import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountService {

    AccountResponseDto save(AccountRequestDto accountDto);

    AccountResponseDto update(Long id, AccountUpdateRequestDto accountDto);

    AccountResponseDto getById(Long id);

    List<AccountAdminResponseDto> getAll();

}
