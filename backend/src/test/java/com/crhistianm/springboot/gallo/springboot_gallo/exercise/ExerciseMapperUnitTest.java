package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExerciseMapperUnitTest {

    @Test
    void shouldReturnResponeDto() {
        final String name = "exercise";
        final String description = "description";
        final boolean weightRequired = false;
        final String imageUrl = "url";

        final Exercise entity = new Exercise(name, description, weightRequired, imageUrl);

        ExerciseResponseDto expectedResponse = ExerciseMapper.entityToResponse(entity);

        assertThat(expectedResponse).extracting(ExerciseResponseDto::getName).isEqualTo(name);
        assertThat(expectedResponse).extracting(ExerciseResponseDto::getImageUrl).isEqualTo(imageUrl);
        assertThat(expectedResponse).extracting(ExerciseResponseDto::getDescription).isEqualTo(description);
        assertThat(expectedResponse).extracting(ExerciseResponseDto::isWeightRequired).isEqualTo(weightRequired);
    }
   
}
