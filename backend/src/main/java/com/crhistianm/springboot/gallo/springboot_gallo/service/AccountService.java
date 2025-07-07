package com.crhistianm.springboot.gallo.springboot_gallo.service;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountService {

    Account save(AccountDto acocunt);
    
}
