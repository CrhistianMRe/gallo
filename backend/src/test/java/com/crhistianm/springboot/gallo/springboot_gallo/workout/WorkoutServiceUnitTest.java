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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.Exercise;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

import jakarta.persistence.EntityManager;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.getAccountInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseData.getExerciseInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceUnitTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutValidator workoutValidator;

    @Mock
    private EntityManager entityManager;

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
            assertThat(listResponse).extracting(WorkoutResponseDto::getWorkoutLength).contains((short)60).hasSize(4);
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

    @Nested
    class CreateModuleTest {

        WorkoutRequestDto workoutRequestDto;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                Long accountId = invo.getArgument(0, WorkoutRequestDto.class).getAccountId();
                if(accountId.equals(2L)) throw new ValidationServiceException("Error");
                return null;
            }).when(workoutValidator).validateRequest(any(WorkoutRequestDto.class));


            lenient().doAnswer(invo -> {
                Account account = getAccountInstance();
                account.setId(1L);
                return account;
            }).when(entityManager).getReference(eq(Account.class), anyLong());

            lenient().doAnswer(invo -> {
                Exercise exercise = getExerciseInstance();
                exercise.setImageUrl("imageUrl");
                exercise.setName("exercise name");
                return exercise;
            }).when(entityManager).getReference(eq(Exercise.class), anyLong());

            lenient().doAnswer(invo -> {
                Workout workout = invo.getArgument(0, Workout.class);
                workout.getExercise().setName("saved " + workout.getExercise().getName());
                workout.setId(1L);
                return workout;
            }).when(workoutRepository).save(any(Workout.class));

        }

        @Test
        void shouldReturnResponseDtoWhenRequestIsValid() {
            Long accountId = 20L;

            LocalDate workoutDate = LocalDate.of(2004, 9, 28);

            short workoutLength = 60;
 
            Long exerciseId = 10L;

            workoutRequestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            WorkoutResponseDto expectedResponse = workoutService.save(workoutRequestDto);

            assertThat(expectedResponse).extracting(WorkoutResponseDto::getId).isEqualTo(1L);
            assertThat(expectedResponse).extracting(WorkoutResponseDto::getExerciseName).isEqualTo("saved exercise name");
            assertThat(expectedResponse).extracting(WorkoutResponseDto::getImageUrl).isEqualTo("imageUrl");
            assertThat(expectedResponse).extracting(WorkoutResponseDto::getWorkoutLength).isEqualTo((short)60);

            verify(workoutValidator, times(1)).validateRequest(eq(workoutRequestDto));
            verify(entityManager, times(1)).getReference(eq(Exercise.class), eq(10L));
            verify(entityManager, times(1)).getReference(eq(Account.class), eq(20L));
            verify(workoutRepository, times(1)).save(any(Workout.class));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            Long accountId = 2L;

            LocalDate workoutDate = LocalDate.of(2004, 9, 28);

            short workoutLength = 60;
 
            Long exerciseId = 10L;

            workoutRequestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            String errorMessage = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutService.save(workoutRequestDto))
                .actual()
                .getMessage();

            assertThat(errorMessage).isEqualTo("Error");

            verify(workoutValidator, times(1)).validateRequest(eq(workoutRequestDto));
            verifyNoInteractions(entityManager);
            verifyNoInteractions(workoutRepository);
        }

    }

}
