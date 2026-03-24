package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;


@ExtendWith(MockitoExtension.class)
class RefreshTokenValidationServiceUnitTest {

    @Mock
    private Environment environment;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    @Spy
    private RefreshTokenValidationService spyTokenValidationService;

    @InjectMocks
    private RefreshTokenValidationService tokenValidationService;

    @Nested
    class IsRefreshTokenExpiredMethod {

        Boolean expectedResult;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);

                RefreshToken refreshToken = null;

                if(argRefreshToken.equals("expired")) {
                    refreshToken = new RefreshTokenBuilder()
                        .token(argRefreshToken)
                        .expiresAt(LocalDateTime.now().minusYears(1))
                        .build();
                }

                if(argRefreshToken.equals("notexpired")) {
                    refreshToken = new RefreshTokenBuilder()
                        .token(argRefreshToken)
                        .expiresAt(LocalDateTime.now().plusYears(1))
                        .build();
                }

                return Optional.ofNullable(refreshToken);
            }).when(refreshTokenRepository).findByToken(anyString());


        }

        @Test
        void shouldReturnTrueWhenRefreshTokenIsExpired() {
            final String refreshToken = "expired";

            expectedResult = tokenValidationService.isRefreshTokenExpired(refreshToken);

            assertThat(expectedResult).isTrue();

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }

        @Test
        void shouldReturnFalseWhenRefreshTokenIsNotExpired() {
            final String refreshToken = "notexpired";

            expectedResult = tokenValidationService.isRefreshTokenExpired(refreshToken);

            assertThat(expectedResult).isFalse();

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }


        @Test
        void shouldThrowExceptionWhenRefreshTokenIsNotFound() {
            final String refreshToken = "notfound";

            final String errorMessage = assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> tokenValidationService.isRefreshTokenExpired(refreshToken))
                .actual()
                .getMessage();

            assertThat(errorMessage).isEqualTo("RefreshToken not found");

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }

    }

    @Nested
    class IsRefreshTokenRevokedMethod {

        Boolean expectedResult;

        @BeforeEach
        void setUp() {

            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);

                RefreshToken dbRefreshToken = null;

                if(argRefreshToken.equals("revoked")) {

                    dbRefreshToken = new RefreshTokenBuilder()
                        .revoked(true)
                        .token(argRefreshToken)
                        .build();
                }

                if(argRefreshToken.equals("notrevoked")){

                    dbRefreshToken = new RefreshTokenBuilder()
                        .revoked(false)
                        .token(argRefreshToken)
                        .build();

                }

                return Optional.ofNullable(dbRefreshToken);
            }).when(refreshTokenRepository).findByToken(anyString());

        }

        @Test
        void shouldReturnFalseWhenRefreshTokenIsNotRevoked() {
            final String refreshToken = "notrevoked";

            expectedResult = tokenValidationService.isRefreshTokenRevoked(refreshToken);

            assertThat(expectedResult).isFalse();

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }

        @Test
        void shouldReturnTrueWhenRefreshTokenIsRevoked() {
            final String refreshToken = "revoked";

            expectedResult = tokenValidationService.isRefreshTokenRevoked(refreshToken);

            assertThat(expectedResult).isTrue();

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }

        @Test
        void shouldThrowExceptionWhenRefreshTokenIsNotFound() {
            final String refreshToken = "notfound";

            final String errorMessage = assertThatExceptionOfType(NotFoundException.class).isThrownBy(()->{
                tokenValidationService.isRefreshTokenRevoked(refreshToken);
            })
            .actual().getMessage();

            assertThat(errorMessage).isEqualTo("RefreshToken not found");

            verify(refreshTokenRepository, times(1)).findByToken(eq(refreshToken));
        }

    }

    @Nested
    class ValidateRefreshTokenExpirationDateMethod {

        Optional<FieldInfoError> expectedOptional;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);
                return !argRefreshToken.equals("valid");
            }).when(spyTokenValidationService).isRefreshTokenExpired(anyString());

            lenient().doAnswer(invo -> invo.getArgument(0, String.class)).when(environment).getProperty(anyString());
        }

        @Test
        void shouldReturnOptionalFieldInfoError() {

            final String refreshToken = "invalid";

            expectedOptional = spyTokenValidationService.validateRefreshTokenExpirationDate(refreshToken);

            assertThat(expectedOptional).isNotEmpty();

            FieldInfoError expectedError = expectedOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage)
                .isEqualTo("refreshtoken.validation.RefreshTokenExpired");

            assertThat(expectedError).extracting(FieldInfoError::getName)
                .isEqualTo("refreshToken");

            assertThat(expectedError).extracting(FieldInfoError::getValue)
                .isEqualTo(refreshToken);

            assertThat(expectedError).extracting(FieldInfoError::getType)
                .isEqualTo(refreshToken.getClass());

            verify(environment, times(1)).getProperty(eq("refreshtoken.validation.RefreshTokenExpired"));

            verify(spyTokenValidationService, times(1)).isRefreshTokenExpired(eq(refreshToken));
        }

        @Test
        void shouldReturnEmptyOptionalFieldInfoError() {
            final String refreshToken = "valid";

            expectedOptional = spyTokenValidationService.validateRefreshTokenExpirationDate(refreshToken);

            assertThat(expectedOptional).isEmpty();

            verifyNoInteractions(environment);
            verify(spyTokenValidationService, times(1)).isRefreshTokenExpired(eq(refreshToken));

        }

    }

    @Nested
    class ValidateRefreshTokenRevocation {

        Optional<FieldInfoError> expectedOptional;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);

                return !argRefreshToken.equals("valid");
            }).when(spyTokenValidationService).isRefreshTokenRevoked(anyString());

            lenient().doAnswer(invo -> invo.getArgument(0, String.class)).when(environment).getProperty(anyString());

        }

        @Test
        void shouldReturnEmptyOptionalFieldInfoError() {
            final String refreshToken = "valid";

            expectedOptional = spyTokenValidationService.validateRefreshTokenRevocation(refreshToken);

            assertThat(expectedOptional).isEmpty();


        }

        @Test
        void shouldReturnOptionalFieldInfoError() {

            final String refreshToken = "invalid";

            expectedOptional = spyTokenValidationService.validateRefreshTokenRevocation(refreshToken);

            assertThat(expectedOptional).isNotEmpty();

            FieldInfoError expectedError = expectedOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getValue)
                .isEqualTo(refreshToken);

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage)
                .isEqualTo("refreshtoken.validation.RefreshTokenRevoked");

            assertThat(expectedError).extracting(FieldInfoError::getType)
                .isEqualTo(refreshToken.getClass());

            assertThat(expectedError).extracting(FieldInfoError::getName)
                .isEqualTo("refreshToken");

        }

    }
    
}
