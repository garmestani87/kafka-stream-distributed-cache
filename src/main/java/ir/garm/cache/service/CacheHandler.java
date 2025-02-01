package ir.garm.cache.service;

import ir.garm.cache.domain.dto.AccessTokenDto;
import ir.garm.cache.domain.dto.ResponseData;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public interface CacheHandler<R> {

    default ResponseData<R> generateToken(String... refresh_token) {
        return null;
    }

    default ResponseData<R> getAccessToken() {
        return null;
    }

    default void setCreatedAt(AccessTokenDto dto) {
        if (Objects.nonNull(dto)) {
            dto.setCreateAt(getEpochNow());
        }
    }

    default boolean hasExpiredAccessToken(AccessTokenDto dto) {
        return getEpochNow() >= (dto.getCreateAt() + Long.parseLong(dto.getExpires_in()));
    }

    default boolean hasExpiredRefreshToken(AccessTokenDto dto) {
        return getEpochNow() >= (dto.getCreateAt() + (2 * Long.parseLong(dto.getExpires_in())));
    }

    default Long getEpochNow() {
        return ZonedDateTime.now(ZoneId.of("Etc/UTC")).toEpochSecond();
    }

}
