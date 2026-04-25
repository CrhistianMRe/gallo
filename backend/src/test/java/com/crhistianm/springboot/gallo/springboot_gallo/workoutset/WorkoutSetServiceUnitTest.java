package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.workout.Workout;

import static com.crhistianm.springboot.gallo.springboot_gallo.workoutset.WorkoutSetData.givenSetRequestDtoList;
import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.getWorkoutInstance;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class WorkoutSetServiceUnitTest {

    @Mock
    private WorkoutSetValidator workoutSetValidator;

    @Mock
    private WorkoutSetRepository workoutSetRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WorkoutSetService workoutSetService;

    @Nested
    class CreateModuleTest {

        WorkoutSetRequestDto requestDto;

        @BeforeEach
        void setUp() {
            lenient().doAnswer(invo -> {
                return invo.getArgument(0, List.class);
            }).when(workoutSetRepository).saveAll(anyList());

            doAnswer(invo -> {
                WorkoutSetRequestDto workoutSetRequestDto = invo.getArgument(0, WorkoutSetRequestDto.class);
                if(!workoutSetRequestDto.getWorkoutId().equals(10L)) {
                    throw new ValidationServiceException(List.of(new FieldInfoErrorBuilder().name("error").build()));
                }
                return null;
            }).when(workoutSetValidator).validateSaveAllRequest(any(WorkoutSetRequestDto.class));

            lenient().doReturn(getWorkoutInstance()).when(entityManager).getReference(eq(Workout.class), anyLong());
        }

        @Test
        void shouldReturnResponseDtoWhenRequestIsValid() {
            Long workoutId = 10L;

            requestDto = new WorkoutSetRequestDto(workoutId, givenSetRequestDtoList());

            List<WorkoutSetResponseDto> expectedListResponse = workoutSetService.saveAll(requestDto);

            assertThat(expectedListResponse).hasSize(4);

            assertThat(expectedListResponse)
                .filteredOn(set -> set.getRepAmount().equals(20))
                .extracting(WorkoutSetResponseDto::getRepAmount, WorkoutSetResponseDto::getWeightAmount, WorkoutSetResponseDto::isToFailure)
                .containsOnlyOnce(tuple(20, 20.00, true));

            assertThat(expectedListResponse)
                .filteredOn(set -> set.getRepAmount().equals(40))
                .extracting(WorkoutSetResponseDto::getRepAmount, WorkoutSetResponseDto::getWeightAmount, WorkoutSetResponseDto::isToFailure)
                .containsOnlyOnce(tuple(40, 40.00, true));

            assertThat(expectedListResponse)
                .filteredOn(set -> set.getRepAmount().equals(60))
                .extracting(WorkoutSetResponseDto::getRepAmount, WorkoutSetResponseDto::getWeightAmount, WorkoutSetResponseDto::isToFailure)
                .containsOnlyOnce(tuple(60, 60.00, true));

            assertThat(expectedListResponse)
                .filteredOn(set -> set.getRepAmount().equals(80))    
                .extracting(WorkoutSetResponseDto::getRepAmount, WorkoutSetResponseDto::getWeightAmount, WorkoutSetResponseDto::isToFailure)
                .containsOnlyOnce(tuple(80, 80.00, false));

            verify(entityManager, times(1)).getReference(eq(Workout.class), eq(requestDto.getWorkoutId()));
            verify(workoutSetRepository, times(1)).saveAll(anyList());
            verify(workoutSetValidator, times(1)).validateSaveAllRequest(eq(requestDto));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            Long workoutId = 11L;

            requestDto = new WorkoutSetRequestDto(workoutId, givenSetRequestDtoList());

            List<FieldInfoError> expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetService.saveAll(requestDto)).actual().getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            FieldInfoError expectedError = expectedErrors.get(0);

            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("error");

            verify(workoutSetValidator, times(1)).validateSaveAllRequest(eq(requestDto));
            verifyNoInteractions(entityManager);
            verifyNoInteractions(workoutSetRepository);
        }

    }

    @Nested
    class ViewModuleTest {

        Long workoutId;

        @BeforeEach
        void setUp() {
            lenient().doAnswer(invo -> {
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                List<WorkoutSet> entityList = new ArrayList<>();

                if(argWorkoutId.equals(1L)) {
                    WorkoutSet set1 = new WorkoutSet();
                    set1.setWeightAmount(10.00);
                    set1.setToFailure(false);
                    set1.setRepAmount((byte)10);

                    WorkoutSet set2 = new WorkoutSet();
                    set2.setWeightAmount(20.00);
                    set2.setToFailure(true);
                    set2.setRepAmount((byte)20);

                    entityList.add(set1);
                    entityList.add(set2);
                }

                return entityList;
            }).when(workoutSetRepository).findAllByWorkoutId(anyLong());

            doAnswer(invo -> {
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(10L)) throw new ValidationServiceException();
                return null;
            }).when(workoutSetValidator).validateByIdRequest(anyLong());

        }

        @Test
        void shouldReturnEmptyWorkoutSetList() {
            workoutId = 2L;

            List<WorkoutSetResponseDto> responseList = workoutSetService.getAllByWorkoutId(workoutId);

            assertThat(responseList).isEmpty();

            verify(workoutSetRepository, times(1)).findAllByWorkoutId(eq(workoutId));
            verify(workoutSetValidator).validateByIdRequest(eq(workoutId));
        }

        @Test
        void shouldReturnFilledWorkoutSetList() {
            workoutId = 1L;

            List<WorkoutSetResponseDto> responseList = workoutSetService.getAllByWorkoutId(workoutId);

            assertThat(responseList).isNotEmpty();

            verify(workoutSetRepository, times(1)).findAllByWorkoutId(eq(workoutId));
            verify(workoutSetValidator).validateByIdRequest(eq(workoutId));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            workoutId = 10L;

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetService.getAllByWorkoutId(workoutId));

            verifyNoInteractions(workoutSetRepository);
            verify(workoutSetValidator).validateByIdRequest(eq(workoutId));
        }

    }
    
}
