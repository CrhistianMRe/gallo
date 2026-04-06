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
        short workoutLength = 60;
        LocalDate workoutDate = LocalDate.of(2004, 9, 28);
        Long exerciseId = null;
        Long accountId = null;

        WorkoutRequestDto requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

        Workout expectedEntity = WorkoutMapper.requestToEntity(requestDto);

        assertThat(expectedEntity).extracting(Workout::getWorkoutDate).isEqualTo(workoutDate);
        assertThat(expectedEntity).extracting(Workout::getWorkoutLength).isEqualTo(workoutLength);
    }
    
}
