package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.getAccountInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.RoleData.getRoleInstance;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import javax.management.relation.RoleList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.account.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceUnitTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenValidator refreshTokenValidator;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Nested
    class RefreshAccessTokenMethod {  

        RefreshTokenRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new RefreshTokenRequestDto("refreshtoken");

            doAnswer(invo -> {
                final String argToken = invo.getArgument(0, String.class);

                RefreshToken refreshTokenResponse = null;

                if(argToken.equals("refreshtoken")) {
                    refreshTokenResponse = new RefreshToken();
                    Account accountResponse = getAccountInstance();
                    Role role = getRoleInstance();
                    role.setName("adminrole");
                    accountResponse.addRole(role);
                    accountResponse.setEmail("email");
                    refreshTokenResponse.setAccount(accountResponse);
                }

                if(argToken.equals("json")) {
                    refreshTokenResponse = new RefreshToken();
                    Account accountResponse = getAccountInstance();

                    accountResponse.setEmail("email");
                    refreshTokenResponse.setAccount(accountResponse);
                }

                return Optional.ofNullable(refreshTokenResponse);
            }).when(refreshTokenRepository).findByToken(anyString());

        }

        @Test 
        void shouldThrowNotFoundException () {
            requestDto = new RefreshTokenRequestDto("notfound");

            String message = assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> refreshTokenService.refreshAccessToken(requestDto)).actual().getMessage();

            assertThat(message).isEqualTo("RefreshToken not found");

            verify(refreshTokenRepository, times(1)).findByToken(requestDto.getRefreshToken());
        }

        @Test
        void shouldReturnValidRefreshToken() {
            RefreshTokenResponseDto responseDto = refreshTokenService.refreshAccessToken(requestDto);

            assertThat(responseDto).extracting(RefreshTokenResponseDto::getAccessToken).asString().startsWith("eyJ");
            assertThat(responseDto).extracting(RefreshTokenResponseDto::getExpiresAt).asString().isNotEmpty();

            verify(refreshTokenRepository, times(1)).findByToken(requestDto.getRefreshToken());
        }

    } 

    @Nested
    class CreateRefreshTokenMethod {


        @Test
        void shouldReturnRefreshToken() {
            final Long accountID = 1L;


            final String responseRefreshToken = refreshTokenService.createRefreshToken(accountID);

            assertThat(responseRefreshToken).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        }

    }
    
}
