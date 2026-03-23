package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(controllers = {RefreshTokenController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(JacksonConfig.class)
class RefreshTokenControllerTest {

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    private RefreshTokenRequestDto requestDto;

    @BeforeEach
    void setUp() {
        doAnswer(invo -> {
            Map<String, String> responseMap = new HashMap<>();

            responseMap.put("accessToken", "valid");

            return responseMap;
        }).when(refreshTokenService).refreshAccessToken(any(RefreshTokenRequestDto.class));

    }

    @Test
    void shouldReturnRefreshTokenResponse() throws Exception {
        final String refreshToken = "validrefreshtoken";
        requestDto = new RefreshTokenRequestDto(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("valid"));

    }

}
