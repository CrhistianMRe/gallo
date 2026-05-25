package com.crhistianm.springboot.gallo.springboot_gallo.shared;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheHandlingUtils;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheResponseContext;

@ExtendWith(MockitoExtension.class)
public class CacheHandlingUtilsTest {

    @Mock
    private Cache cache;

    //Testeas esto y renameas la branch a perf/person-cache y haces la PR. Despues tenes el stash para una nueva PR de account cache
    private class TestDto implements ResponseDto {

        private Long id;

        private String example;

        public TestDto(String example, Long id) { this.example = example; this.id = id; }

        public String getExample() { return example; }

    }

    private CacheResponseContext<TestDto> cacheContext;

    private final Long responseId = 1L;

    @BeforeEach
    void setUp(){ clearInvocations(cache); }

    @Test
    void shouldReturnCachedResponseWhenCacheHits(){
        final String example = "cached";
        TestDto responseDto = new TestDto(example, responseId);

        doReturn(responseDto).when(cache).get(anyLong(), eq(TestDto.class));

        cacheContext = CacheResponseContext
            .<TestDto>builder()
            .responseType(TestDto.class)
            .keyId(responseId)
            .cache(cache)
            .onMissDo(() -> null)
            .build();


        TestDto expectedResponse = CacheHandlingUtils.getOrCacheResponse(cacheContext);

        assertThat(expectedResponse).extracting(TestDto::getExample).isEqualTo("cached");

        verify(cache, times(1)).get(eq(responseId), eq(TestDto.class));
        verifyNoMoreInteractions(cache);
    }

    @Test
    void shouldCacheAndReturnSupplierResponseWhenCacheMisses(){
        final String example = "toCache";
        TestDto responseDto = new TestDto(example, responseId);

        doReturn(null).when(cache).get(anyLong(), eq(TestDto.class));

        cacheContext = CacheResponseContext
            .<TestDto>builder()
            .responseType(TestDto.class)
            .keyId(responseId)
            .cache(cache)
            .onMissDo(() -> responseDto)
            .build();


        TestDto expectedResponse = CacheHandlingUtils.getOrCacheResponse(cacheContext);

        assertThat(expectedResponse).extracting(TestDto::getExample).isEqualTo("toCache");

        verify(cache, times(1)).get(eq(responseId), eq(TestDto.class));
        verify(cache, times(1)).put(eq(responseId), eq(responseDto));
    }

    @Test
    void shouldReturnAndReturnSupplierResponseWhenCacheIsNull() {
        final String example = "supplier";
        TestDto responseDto = new TestDto(example, responseId);

        cacheContext = CacheResponseContext
            .<TestDto>builder()
            .responseType(TestDto.class)
            .keyId(responseId)
            .cache(null)
            .onMissDo(() -> responseDto)
            .build();

        TestDto expectedResponse = CacheHandlingUtils.getOrCacheResponse(cacheContext);

        assertThat(expectedResponse).extracting(TestDto::getExample).isEqualTo("supplier");

    }


}
