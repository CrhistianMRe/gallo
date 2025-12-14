package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenWorkout;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.WorkoutResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;

public class WorkoutMapperUnitTest {

    @Test
    void shouldReturnResponseDtoFromEntity() {
        Workout entity = givenWorkout().orElseThrow();
        entity.setId(1L);

        WorkoutResponseDto responseDto = WorkoutMapper.entityToResponse(entity);

        assertThat(responseDto).extracting(WorkoutResponseDto::getExerciseName).isEqualTo(entity.getExercise().getName());
        assertThat(responseDto).extracting(WorkoutResponseDto::getImageUrl).isEqualTo(entity.getExercise().getImageUrl());
        assertThat(responseDto).extracting(WorkoutResponseDto::getWorkoutLength).isEqualTo(entity.getWorkoutLength());
        assertThat(responseDto).extracting(WorkoutResponseDto::getId).isEqualTo(entity.getId());
        assertThat(responseDto).extracting(WorkoutResponseDto::getWorkoutDate).isEqualTo(entity.getWorkoutDate());
    }
    
}
