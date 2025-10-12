package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class RoleMapperUnitTest {

    @Test
    void returnsRoleEntityFromRoleRequestDto(){

        RoleRequestDto roleRequestDto = new RoleRequestDto(1L, "role1");

        Role expectedRole = RoleMapper.requestToEntity(roleRequestDto);

        assertThat(expectedRole).extracting(Role::getId).isEqualTo(1L);
        assertThat(expectedRole).extracting(Role::getName).isEqualTo("role1");
    }

    
}
