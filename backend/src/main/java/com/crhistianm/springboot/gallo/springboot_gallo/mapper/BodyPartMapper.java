package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.BodyPartResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.BodyPart;

public class BodyPartMapper {

    public static BodyPartResponseDto entityToResponse(BodyPart entity) {
        BodyPartResponseDto response = new BodyPartResponseDto();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

}
