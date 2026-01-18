package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutData.givenWorkoutList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;


@WebMvcTest(controllers = {WorkoutController.class})
@Import(value = {JacksonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerTest {

    @MockitoBean
    private WorkoutService workoutService;

    @Autowired
    MockMvc mockMvc;

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

}
