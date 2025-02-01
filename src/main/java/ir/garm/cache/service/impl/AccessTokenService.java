package ir.garm.cache.service.impl;

import ir.garm.cache.domain.dto.AccessTokenDto;
import ir.garm.cache.domain.dto.ResponseData;
import ir.garm.cache.service.CacheHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static ir.garm.cache.domain.constant.Constants.CACHE_KEY_NAME;
import static ir.garm.cache.domain.constant.Constants.CACHE_STORE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService implements CacheHandler<AccessTokenDto> {

    private final AccessTokenCacheService cacheService;
    private final StreamsBuilderFactoryBean streamsBuilderFactory;


    @Override
    public ResponseData<AccessTokenDto> getAccessToken() {

        AccessTokenDto tokenDto = retrieveAccessToken();

        ResponseData<AccessTokenDto> response = null;
        if (Objects.nonNull(tokenDto)) {
            if (hasExpiredAccessToken(tokenDto)) {
                if (hasExpiredRefreshToken(tokenDto)) {
                    response = cacheService.generateToken();
                } else {
                    response = cacheService.generateToken(tokenDto.getRefresh_token());
                }
            }
        } else {
            response = cacheService.generateToken();
        }

        return Objects.nonNull(response)
                ? response
                : new ResponseData<AccessTokenDto>().setResponse(tokenDto).setStatus(HttpStatus.OK);
    }

    private AccessTokenDto retrieveAccessToken() {
        ReadOnlyKeyValueStore<String, AccessTokenDto> tokenStore = streamsBuilderFactory
                .getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(CACHE_STORE_NAME, QueryableStoreTypes.keyValueStore()));

        return tokenStore.get(CACHE_KEY_NAME);
    }

}
