package com.crhistianm.springboot.gallo.springboot_gallo.shared.cache;

import org.springframework.cache.Cache;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.ResponseDto;

import static java.util.Objects.nonNull;

public class CacheHandlingUtils {

    public static <T extends ResponseDto> T getOrCacheResponse(CacheResponseContext<T> context) {
        final Cache cache = context.getCache();
        if(nonNull(cache)) {
            T cachedResponse = cache.get(context.getKeyId(), context.getResponseType());
            if(nonNull(cachedResponse)) return cachedResponse;
        }

        T responseDto = context.getOnMissDo().get();

        if(nonNull(cache)) cache.put(context.getKeyId(), responseDto);

        return responseDto;
    }
    
}


