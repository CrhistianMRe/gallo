package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

@ExtendWith(MockitoExtension.class)
class RefreshTokenValidatorUnitTest {

    @Mock
    private RefreshTokenValidationService tokenValidationService;

    @InjectMocks
    private RefreshTokenValidator tokenValidator;

    @Nested
    class RefreshTokenValidationServiceMethod {

        List<FieldInfoError> expectedErrors;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);

                FieldInfoError returningError = null;

                if(argRefreshToken.contains("expired")) {
                    returningError = new FieldInfoErrorBuilder()
                        .name(argRefreshToken)
                        .build();
                }

                return Optional.ofNullable(returningError);
            }).when(tokenValidationService).validateRefreshTokenExpirationDate(anyString());

            doAnswer(invo -> {
                final String argRefreshToken = invo.getArgument(0, String.class);

                FieldInfoError returningError = null;

                if(argRefreshToken.contains("revoked")) {
                    returningError = new FieldInfoErrorBuilder()
                        .name(argRefreshToken)
                        .build();
                }

                return Optional.ofNullable(returningError);
            }).when(tokenValidationService).validateRefreshTokenRevocation(anyString());

        }

        @Test
        void shouldNotThrowExceptionWhenAllIsValid() {
            final String refreshToken = "allvalid";

            assertThatCode(() -> tokenValidator.validateTokenRefresh(refreshToken))
                .doesNotThrowAnyException();

            verify(tokenValidationService, times(1)).validateRefreshTokenExpirationDate(eq(refreshToken));
            verify(tokenValidationService, times(1)).validateRefreshTokenRevocation(eq(refreshToken));
        }

        @Test
        void shouldThrowExceptionWhenAllErrorsMatch() {
            final String refreshToken = "revoked and expired";

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> tokenValidator.validateTokenRefresh(refreshToken))
                .actual()
                .getFieldErrors();

            assertThat(expectedErrors).isNotEmpty();

            assertThat(expectedErrors).hasSize(2);

            assertThat(expectedErrors).extracting(FieldInfoError::getName)
                .containsOnly(refreshToken);

            verify(tokenValidationService, times(1)).validateRefreshTokenExpirationDate(eq(refreshToken));
            verify(tokenValidationService, times(1)).validateRefreshTokenRevocation(eq(refreshToken));
        }

        @Test
        void shouldThrowExceptionWhenExpirationIsInvalid() {
            final String refreshToken = "expired";

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> tokenValidator.validateTokenRefresh(refreshToken))
                .actual()
                .getFieldErrors();

            assertThat(expectedErrors).isNotEmpty();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName)
                .containsOnly("expired");

            verify(tokenValidationService, times(1)).validateRefreshTokenExpirationDate(eq(refreshToken));
            verify(tokenValidationService, times(1)).validateRefreshTokenRevocation(eq(refreshToken));

        }

        @Test
        void shouldThrowExceptionWhenRevocationIsConceded() {
            final String refreshToken = "revoked";

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> tokenValidator.validateTokenRefresh(refreshToken))
                .actual()
                .getFieldErrors();

            assertThat(expectedErrors).isNotEmpty();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName)
                .containsOnly("revoked");

            verify(tokenValidationService, times(1)).validateRefreshTokenExpirationDate(eq(refreshToken));
            verify(tokenValidationService, times(1)).validateRefreshTokenRevocation(eq(refreshToken));

        }

    }
    
}
