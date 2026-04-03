package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

class WorkoutSetMapper {

    static WorkoutSet dtoToEntity(SetRequestDto dto) {
        return new WorkoutSetBuilder()
            .repAmount(dto.getRepAmount().byteValue())
            .weightAmount(dto.getWeightAmount())
            .toFailure(dto.isToFailure())
            .build();
    }

    static WorkoutSetResponseDto entityToResponse(WorkoutSet entity) {
        WorkoutSetResponseDto responseDto = new WorkoutSetResponseDto();
        responseDto.setToFailure(entity.getToFailure());
        responseDto.setWeightAmount(entity.getWeightAmount());
        responseDto.setRepAmount(entity.getRepAmount().intValue());
        return responseDto;
    }

}
