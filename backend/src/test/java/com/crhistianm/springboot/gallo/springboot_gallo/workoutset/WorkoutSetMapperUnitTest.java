package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkoutSetMapperUnitTest {


    @Test
    void shouldReturnEntityFromDto() {
        Integer repAmount = 40;
        Double weightAmount = 40.00;
        boolean toFailure = false;

        SetRequestDto workoutSetDto = new SetRequestDto(repAmount, weightAmount, toFailure);

        WorkoutSet expectedEntity = WorkoutSetMapper.dtoToEntity(workoutSetDto);

        assertThat(expectedEntity).extracting(WorkoutSet::getRepAmount).isEqualTo((byte)40);
        assertThat(expectedEntity).extracting(WorkoutSet::getToFailure).isEqualTo(false);
        assertThat(expectedEntity).extracting(WorkoutSet::getWeightAmount).isEqualTo(40.00);
    }

    @Test
    void shouldReturnResponseDtoFromEntity() {
        WorkoutSet workoutEntity = new WorkoutSet();
        workoutEntity.setRepAmount((byte)80);
        workoutEntity.setWeightAmount(80.00);
        workoutEntity.setToFailure(true);

        WorkoutSetResponseDto expectedResponse = WorkoutSetMapper.entityToResponse(workoutEntity);

        assertThat(expectedResponse).extracting(WorkoutSetResponseDto::getRepAmount).isEqualTo(80);
        assertThat(expectedResponse).extracting(WorkoutSetResponseDto::isToFailure).isEqualTo(true);
        assertThat(expectedResponse).extracting(WorkoutSetResponseDto::getWeightAmount).isEqualTo(80.00);
    }
    
}
