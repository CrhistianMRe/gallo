package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static com.crhistianm.springboot.gallo.springboot_gallo.workoutset.WorkoutSetData.givenSetRequestDtoList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {WorkoutSetController.class})
@Import(value = JacksonConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class WorkoutSetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutSetService workoutSetService;

    @InjectMocks
    private WorkoutSetController workoutSetController;

    @Nested
    class CreateModuleTest {

        WorkoutSetRequestDto requestDto;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                WorkoutSetRequestDto argRequest = invo.getArgument(0, WorkoutSetRequestDto.class);
                List<WorkoutSetResponseDto> responseDtos = argRequest
                    .getSets().stream().map(WorkoutSetMapper::dtoToEntity).collect(Collectors.toList())
                    .stream().map(WorkoutSetMapper::entityToResponse).collect(Collectors.toList());
                return responseDtos;
            }).when(workoutSetService).saveAll(any(WorkoutSetRequestDto.class));
        }

        @Test
        void shouldReturnResponseWithCreatedStatus() throws Exception {
            Long workoutId = 1L;

            requestDto = new WorkoutSetRequestDto(workoutId, givenSetRequestDtoList());

            mockMvc.perform(post("/api/workout-sets")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("[0].repAmount").value(20))
                .andExpect(jsonPath("[0].weightAmount").value(20.00))
                .andExpect(jsonPath("[0].toFailure").value(true))

                .andExpect(jsonPath("[1].repAmount").value(40))
                .andExpect(jsonPath("[1].weightAmount").value(40.00))
                .andExpect(jsonPath("[1].toFailure").value(true))

                .andExpect(jsonPath("[2].repAmount").value(60))
                .andExpect(jsonPath("[2].weightAmount").value(60.00))
                .andExpect(jsonPath("[2].toFailure").value(true))

                .andExpect(jsonPath("[3].repAmount").value(80))
                .andExpect(jsonPath("[3].weightAmount").value(80.00))
                .andExpect(jsonPath("[3].toFailure").value(false));
        }

        @Test
        void shouldReturnDtoException() throws Exception {
            Long workoutId = null;

            requestDto = new WorkoutSetRequestDto(workoutId, givenSetRequestDtoList());

            mockMvc.perform(post("/api/workout-sets")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.workoutId").value("the field workoutId must not be null"));
        }
    
    }

    @Nested
    class ViewModuleTest {  

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                List<WorkoutSetResponseDto> responseList = new ArrayList<>();

                Integer repAmount = 10;
                Double weightAmount = 10.00;
                boolean toFailure = false;

                WorkoutSetResponseDto responseDto = new WorkoutSetResponseDto(repAmount, weightAmount, toFailure);

                repAmount = 20;
                weightAmount = 20.00;
                toFailure = true;

                WorkoutSetResponseDto responseDto2 = new WorkoutSetResponseDto(repAmount, weightAmount, toFailure);

                responseList.add(responseDto);
                responseList.add(responseDto2);

                return responseList;
            }).when(workoutSetService).getAllByWorkoutId(anyLong());

        }

        @Test
        void shouldReturnResponseList() throws Exception {
            mockMvc.perform(get("/api/workout-sets?workoutId=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].repAmount").value(10))
                .andExpect(jsonPath("[0].weightAmount").value(10.00))
                .andExpect(jsonPath("[0].toFailure").value(false))
                .andExpect(jsonPath("[1].repAmount").value(20))
                .andExpect(jsonPath("[1].weightAmount").value(20.00))
                .andExpect(jsonPath("[1].toFailure").value(true));
        }

    }

}
