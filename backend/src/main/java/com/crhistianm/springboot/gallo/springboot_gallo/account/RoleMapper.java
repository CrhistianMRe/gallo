package com.crhistianm.springboot.gallo.springboot_gallo.account;

class RoleMapper {

    static Role requestToEntity(RoleRequestDto roleDto) {
        return new RoleBuilder()
            .id(roleDto.getId())
            .name(roleDto.getName())
            .build();
    }

    
}
