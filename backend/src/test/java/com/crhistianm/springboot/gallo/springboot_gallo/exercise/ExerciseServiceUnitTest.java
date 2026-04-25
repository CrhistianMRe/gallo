package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceUnitTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        doAnswer(invo -> {
            String name = "one";
            String description = "des";
            boolean weightRequired = false;
            String imageUrl = "url";
            Exercise exercise1 = new Exercise(name, description, weightRequired, imageUrl);
            Exercise exercise2 = new Exercise(name, description, weightRequired, imageUrl);
            return List.of(exercise1, exercise2);
        }).when(exerciseRepository).findAll();
    }

    @Test
    void shouldReturnResponseList() {
        List<ExerciseResponseDto> responseDto = exerciseService.getAll();

        assertThat(responseDto).hasSize(2);

        verify(exerciseRepository, times(1)).findAll();
    }
    
}
