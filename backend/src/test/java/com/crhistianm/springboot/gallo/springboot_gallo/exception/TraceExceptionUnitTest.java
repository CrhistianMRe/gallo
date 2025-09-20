package com.crhistianm.springboot.gallo.springboot_gallo.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

public class TraceExceptionUnitTest {

    TraceException thrownTraceException;

    @Test
    void shouldLoadDefaultMethodSourceNameWhenPrefixIsNotFound() {
        thrownTraceException = assertThatExceptionOfType(TraceException.class).isThrownBy(() -> {
            throw new TraceException("test", "wawawawawawawawaw");
        }).actual();
        assertThat(thrownTraceException.getMethodSourceName()).isEqualTo("method name not found");
    }

    @Test
    void shouldLoadMessageWhenIsGivenThroughConstructor() {
        thrownTraceException = assertThatExceptionOfType(TraceException.class).isThrownBy(() -> {
            throw new TraceException("test", "should");
        }).actual();
        assertThat(thrownTraceException.getMessage()).isEqualTo("test");
    }

    @Test
    void shouldLoadMethodSourceNameWhenPrefixIsGivenThroughConstructor() {
        thrownTraceException = assertThatExceptionOfType(TraceException.class).isThrownBy(() -> {
            throw new TraceException("test", "should");
        }).actual();
        assertThat(thrownTraceException.getMethodSourceName()).contains("shouldLoadMethodSourceNameWhenPrefixIsGivenThroughConstructor");
    }


    
}
