package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PagedModel.PageMetadata;

import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceUnitTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutValidator workoutValidator;

    @InjectMocks
    private WorkoutService workoutService;

    @Nested
    class ViewModuleTest {

        Long accountId;

        @BeforeEach
        void setUp() {
            accountId = null;

            doAnswer(invo -> {
                if(!invo.getArgument(0, Long.class).equals(1L)) throw new Exception();
                return null;
            }).when(workoutValidator).validateByIdRequest(anyLong());

            lenient().doAnswer(invo -> {
                Pageable pageable = invo.getArgument(1, Pageable.class);
                List<Workout> workoutList = givenWorkoutList();
                return new PageImpl<>(workoutList, pageable, workoutList.size());
            }).when(workoutRepository).findByAccountId(anyLong(), any(Pageable.class));
        }

        @Test
        void shouldReturnDtoPageModelWhenRequestIsValid() {
            accountId = 1L;

            PagedModel<WorkoutResponseDto> modelResponse = workoutService.getByAccountId(accountId, 0, 1);

            PageMetadata pageResponseData = modelResponse.getMetadata();
            List<WorkoutResponseDto> listResponse = modelResponse.getContent();

            assertThat(pageResponseData.totalElements()).isEqualTo(listResponse.size());
            assertThat(pageResponseData.totalPages()).isEqualTo(4);
            assertThat(pageResponseData.number()).isEqualTo(0);
            assertThat(pageResponseData.size()).isEqualTo(1);

            assertThat(listResponse.size()).isEqualTo(4);

            assertThat(listResponse).extracting(WorkoutResponseDto::getId).containsOnlyOnce(1L);
            assertThat(listResponse).extracting(WorkoutResponseDto::getId).containsOnlyOnce(2L);
            assertThat(listResponse).extracting(WorkoutResponseDto::getId).containsOnlyOnce(3L);
            assertThat(listResponse).extracting(WorkoutResponseDto::getId).containsOnlyOnce(4L);

            assertThat(listResponse).extracting(WorkoutResponseDto::getExerciseName).contains("Leg press").hasSize(4);
            assertThat(listResponse).extracting(WorkoutResponseDto::getImageUrl).contains("imageUrl.com").hasSize(4);
            assertThat(listResponse).extracting(WorkoutResponseDto::getWorkoutLength).contains(120.0).hasSize(4);
            assertThat(listResponse).extracting(WorkoutResponseDto::getWorkoutDate).contains(LocalDate.of(2000, 01, 01)).hasSize(4);

            verify(workoutValidator, times(1)).validateByIdRequest(eq(accountId));
            verify(workoutRepository, times(1)).findByAccountId(eq(accountId), eq(PageRequest.of(0, 1)));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            accountId = 2L;
            assertThatExceptionOfType(Exception.class).isThrownBy(() -> workoutService.getByAccountId(accountId, 0, 1));
            verify(workoutValidator, times(1)).validateByIdRequest(eq(accountId));
            verifyNoInteractions(workoutRepository);
        }

    } 


}
