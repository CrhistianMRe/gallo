package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

class ExerciseMapper {

    static ExerciseResponseDto entityToResponse(Exercise entity) {
        ExerciseResponseDto responseDto = new ExerciseResponseDto();

        responseDto.setId(entity.getId());
        responseDto.setName(entity.getName());
        responseDto.setDescription(entity.getDescription());
        responseDto.setWeightRequired(entity.getWeightRequired());
        responseDto.setImageUrl(entity.getImageUrl());

        return responseDto;
    }
    
}
