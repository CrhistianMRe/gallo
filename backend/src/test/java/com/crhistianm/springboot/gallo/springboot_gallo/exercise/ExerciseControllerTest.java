package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;

@Import(JacksonConfig.class)
@WebMvcTest(controllers = {ExerciseController.class})
@AutoConfigureMockMvc(addFilters = false)
public class ExerciseControllerTest {

    @MockitoBean
    private ExerciseService exerciseService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        doAnswer(invo -> {
            Long id = 1L;
            String name = "one";
            String description = "des";
            boolean weightRequired = false;
            String imageUrl = "url";

            ExerciseResponseDto exercise1 = new ExerciseResponseDto(id, name, description, weightRequired, imageUrl);

            id = 2L;
            name = "two";
            description = "des2";
            weightRequired = true;
            imageUrl = "url2";

            ExerciseResponseDto exercise2 = new ExerciseResponseDto(id, name, description, weightRequired, imageUrl);

            return List.of(exercise1, exercise2);
        }).when(exerciseService).getAll();

    }

    @Test
    void shouldReturnResponseDtoList() throws Exception {
        mockMvc.perform(get("/api/exercises"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].id").value(1L))
            .andExpect(jsonPath("[0].name").value("one"))
            .andExpect(jsonPath("[0].description").value("des"))
            .andExpect(jsonPath("[0].weightRequired").value(false))
            .andExpect(jsonPath("[0].imageUrl").value("url"))
            .andExpect(jsonPath("[1].id").value(2L))
            .andExpect(jsonPath("[1].name").value("two"))
            .andExpect(jsonPath("[1].description").value("des2"))
            .andExpect(jsonPath("[1].weightRequired").value(true))
            .andExpect(jsonPath("[1].imageUrl").value("url2"));
    }

}
