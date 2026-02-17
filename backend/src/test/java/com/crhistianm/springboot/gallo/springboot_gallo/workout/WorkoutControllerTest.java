package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseData.getExerciseInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.givenWorkoutList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintValidatorContext;

import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.Exercise;


@WebMvcTest(controllers = {WorkoutController.class})
@Import(value = {JacksonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerTest {

    @MockitoBean
    private WorkoutService workoutService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AccountUserDetailsService service;

    @Nested
    class ViewModuleTest {

        @BeforeEach
        void setUp() {
            doReturn(new PagedModel<>(new PageImpl<>(givenWorkoutList().stream().map(WorkoutMapper::entityToResponse).collect(Collectors.toList()))))
                .when(workoutService).getByAccountId(anyLong(), anyInt(), anyInt());
        }

        @Test
        void shouldReturnPageModelDtoWithOkStatus() throws Exception {
            int page = 0;
            int size = 4;
            Long accountId = 1L;

            String uri = String.format("/api/workouts/%d?page=%d&size=%d", accountId, page, size);

            mockMvc.perform(request(HttpMethod.GET, uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(Matchers.equalTo(1)))
                .andExpect(jsonPath("$.content[0].workoutDate").value(Matchers.contains(2000, 01, 01)))
                .andExpect(jsonPath("$.content[0].workoutLength").value(Matchers.equalTo(60)))
                .andExpect(jsonPath("$.content[0].exerciseName").value(Matchers.equalTo("Leg press")))
                .andExpect(jsonPath("$.content[0].imageUrl").value(Matchers.equalTo("imageUrl.com")))


                .andExpect(jsonPath("$.content[1].id").value(Matchers.equalTo(2)))
                .andExpect(jsonPath("$.content[1].workoutDate").value(Matchers.contains(2000, 01, 01)))
                .andExpect(jsonPath("$.content[1].workoutLength").value(Matchers.equalTo(60)))
                .andExpect(jsonPath("$.content[1].exerciseName").value(Matchers.equalTo("Leg press")))
                .andExpect(jsonPath("$.content[1].imageUrl").value(Matchers.equalTo("imageUrl.com")))

                .andExpect(jsonPath("$.content[2].id").value(Matchers.equalTo(3)))
                .andExpect(jsonPath("$.content[2].workoutDate").value(Matchers.contains(2000, 01, 01)))
                .andExpect(jsonPath("$.content[2].workoutLength").value(Matchers.equalTo(60)))
                .andExpect(jsonPath("$.content[2].exerciseName").value(Matchers.equalTo("Leg press")))
                .andExpect(jsonPath("$.content[2].imageUrl").value(Matchers.equalTo("imageUrl.com")))


                .andExpect(jsonPath("$.content[3].id").value(Matchers.equalTo(4)))
                .andExpect(jsonPath("$.content[3].workoutDate").value(Matchers.contains(2000, 01, 01)))
                .andExpect(jsonPath("$.content[3].workoutLength").value(Matchers.equalTo(60)))
                .andExpect(jsonPath("$.content[3].exerciseName").value(Matchers.equalTo("Leg press")))
                .andExpect(jsonPath("$.content[3].imageUrl").value(Matchers.equalTo("imageUrl.com")));

            verify(workoutService, times(1)).getByAccountId(accountId, page, size);
        }
    }

    @Nested
    class CreateModuleTest {

        WorkoutRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new WorkoutRequestDto();
            requestDto.setAccountId(1L);
            requestDto.setExerciseId(1L);
            requestDto.setWorkoutLength((short)60);
            requestDto.setWorkoutDate(LocalDate.now());
            doAnswer(invo -> {
                Workout argEntity = WorkoutMapper.requestToEntity(invo.getArgument(0, WorkoutRequestDto.class));
                argEntity.setId(10L);

                Exercise sampleExercise = getExerciseInstance();

                sampleExercise.setName("example exercise");
                sampleExercise.setImageUrl("example url");

                argEntity.setExercise(sampleExercise);
                return WorkoutMapper.entityToResponse(argEntity);
            }).when(workoutService).save(any(WorkoutRequestDto.class));
        }

        @Test
        void shouldReturnResponseWithCreatedStatus() throws Exception {
            mockMvc.perform(post("/api/workouts/register")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Matchers.equalTo(10)))
                .andExpect(jsonPath("$.workoutLength").value(Matchers.equalTo(60)))
                .andExpect(jsonPath("$.workoutDate[0]").value(requestDto.getWorkoutDate().getYear()))
                .andExpect(jsonPath("$.workoutDate[1]").value(requestDto.getWorkoutDate().getMonthValue()))
                .andExpect(jsonPath("$.workoutDate[2]").value(requestDto.getWorkoutDate().getDayOfMonth()))
                .andExpect(jsonPath("$.exerciseName").value(Matchers.equalTo("example exercise")))
                .andExpect(jsonPath("$.imageUrl").value(Matchers.equalTo("example url")));
        }

        @Test
        void shouldReturnDtoException() throws Exception {
            requestDto.setAccountId(null);

            mockMvc.perform(post("/api/workouts/register")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountId").value("the field accountId must not be null"));
        }

    }

}
