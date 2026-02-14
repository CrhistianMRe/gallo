package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.givenWorkout;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class WorkoutMapperUnitTest {

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

    @Test
    void shouldReturnEntityFromRequestDto() {
        final short WORKOUT_LENGTH = 60;
        final LocalDate WORKOUT_DATE = LocalDate.of(2004, 9, 28);

        WorkoutRequestDto requestDto = new WorkoutRequestDto();
        requestDto.setWorkoutLength(WORKOUT_LENGTH);
        requestDto.setWorkoutDate(WORKOUT_DATE);

        Workout expectedEntity = WorkoutMapper.requestToEntity(requestDto);

        assertThat(expectedEntity).extracting(Workout::getWorkoutDate).isEqualTo(WORKOUT_DATE);
        assertThat(expectedEntity).extracting(Workout::getWorkoutLength).isEqualTo(WORKOUT_LENGTH);
    }
    
}
