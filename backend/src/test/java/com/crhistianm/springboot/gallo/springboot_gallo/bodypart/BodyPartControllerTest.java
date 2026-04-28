package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;

@WebMvcTest(controllers = BodyPartController.class)
@Import(value = {JacksonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class BodyPartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BodyPartService bodyPartService;

    //Mock only for SpringSecurityConfig
    @MockitoBean
    private AccountUserDetailsService details;

    @Nested
    class ViewAllMethodTest {

        @BeforeEach
        void setUp() {
            List<BodyPartResponseDto> bodyPartsResponse = new ArrayList<>();
            bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part1").id(1L).build()));
            bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part2").id(2L).build()));
            bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part3").id(3L).build()));
            doReturn(bodyPartsResponse).when(bodyPartService).getAll();
        }

        @Test
        void shouldReturnResponseListWhenRequestIsSent() throws Exception {
            mockMvc.perform(request(HttpMethod.GET, "/api/body-parts"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("part1"))
                .andExpect(jsonPath("[1].name").value("part2"))
                .andExpect(jsonPath("[2].name").value("part3"))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[1].id").value(2L))
                .andExpect(jsonPath("[2].id").value(3L));

            verify(bodyPartService, times(1)).getAll();
        }

    }

    @Nested
    class GetAllByExerciseIdMethodTest {

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                List<BodyPartResponseDto> responseList = new ArrayList<>();

                String name = "part1";
                Long id = 1L;

                BodyPartResponseDto bodyPart1 = new BodyPartResponseDto(name, id);

                name = "part2";
                id = 2L;

                BodyPartResponseDto bodyPart2 = new BodyPartResponseDto(name, id);
                
                responseList.add(bodyPart1);
                responseList.add(bodyPart2);

                return responseList;
            }).when(bodyPartService).getAllByExerciseId(anyLong());
        }

        @Test
        void shouldReturnResponseListWhenRequestIsSent() throws Exception {
            final Long exerciseId = 1L;

            mockMvc.perform(get(String.format("/api/body-parts?exerciseId=%d", exerciseId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("part1"))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[1].name").value("part2"))
                .andExpect(jsonPath("[1].id").value(2));

            verify(bodyPartService, times(1)).getAllByExerciseId(eq(exerciseId));
        }

    }

}
