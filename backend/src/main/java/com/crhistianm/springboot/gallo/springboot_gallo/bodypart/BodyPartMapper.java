package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

class BodyPartMapper {

    static BodyPartResponseDto entityToResponse(BodyPart entity) {
        BodyPartResponseDto response = new BodyPartResponseDto();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

}
