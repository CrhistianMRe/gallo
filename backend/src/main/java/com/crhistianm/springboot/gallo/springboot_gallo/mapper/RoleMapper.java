package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class RoleMapper {

    public static Role requestToEntity(RoleRequestDto roleDto) {
        return new RoleBuilder()
            .id(roleDto.getId())
            .name(roleDto.getName())
            .build();
    }

    
}
