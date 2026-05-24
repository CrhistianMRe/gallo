package com.crhistianm.springboot.gallo.springboot_gallo.shared.cache;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.springframework.cache.Cache;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.ResponseDto;

public final class CacheResponseContext<T extends ResponseDto>{

    private final Long keyId;

    private final Cache cache;

    private final Class<T> responseType;

    private final Supplier<T> onMissDo;

    public Long getKeyId() { return this.keyId; }

    public Cache getCache() { return this.cache; }

    public Class<T> getResponseType() { return this.responseType; }

    public Supplier<T> getOnMissDo(){ return this.onMissDo; }

    private CacheResponseContext
    (
     Long keyId,
     Cache cache,
     Class<T> responseType,
     Supplier<T> onMissDo
    ) {
        this.keyId = keyId;
        this.cache = cache;
        this.responseType = responseType;
        this.onMissDo = onMissDo;
    }

    public static <T extends ResponseDto> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T extends ResponseDto>{

        private Long keyId;

        private Cache cache;

        private Class<T> responseType;

        private Supplier<T> onMissDo;

        private Builder() {}

        public Builder<T> cache(final Cache cache){
            this.cache = cache;
            return this;
        }

        public Builder<T> keyId(final Long keyId) {
            this.keyId = keyId;
            return this;
        }

        public Builder<T> responseType(final Class<T> responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder<T> onMissDo(Supplier<T> onMissDo) {
            this.onMissDo = onMissDo;
            return this;
        }

        public CacheResponseContext<T> build() {
            requireNonNull(this.responseType);
            requireNonNull(this.onMissDo);
            requireNonNull(this.keyId);
            return new CacheResponseContext<>
                (
                 this.keyId,
                 this.cache,
                 this.responseType,
                 this.onMissDo
                );
        }


    }


}
