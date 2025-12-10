package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.WorkoutResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;

public class WorkoutMapper {

    public static WorkoutResponseDto entityToResponse(Workout workout) {
        WorkoutResponseDto responseDto = new WorkoutResponseDto();
        responseDto.setId(workout.getId());
        responseDto.setWorkoutDate(workout.getWorkoutDate());
        responseDto.setWorkoutLength(workout.getWorkoutLength());
        responseDto.setExerciseName(workout.getExercise().getName());
        responseDto.setImageUrl(workout.getExercise().getImageUrl());
        return responseDto;
    }
    
}

