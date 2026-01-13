package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        List<BodyPartResponseDto> bodyPartsResponse = new ArrayList<>();
        bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part1").id(1L).build()));
        bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part2").id(2L).build()));
        bodyPartsResponse.add(BodyPartMapper.entityToResponse(new BodyPartBuilder().name("part3").id(3L).build()));
        doReturn(bodyPartsResponse).when(bodyPartService).getAll();
    }

    @Test
    void shouldReturnResponseListWhenViewAllRequestIsSent() throws Exception {
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
