package com.crhistianm.springboot.gallo.springboot_gallo.service;


import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;

public interface AccountService {

    AccountResponseDto save(AccountRequestDto accountDto);

    AccountResponseDto getById(Long id);

    List<AccountAdminResponseDto> getAll();

}
