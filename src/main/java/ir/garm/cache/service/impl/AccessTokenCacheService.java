package ir.garm.cache.service.impl;

import ir.garm.cache.config.ApplicationProperties;
import ir.garm.cache.domain.dto.AccessTokenDto;
import ir.garm.cache.domain.dto.AccessTokenErrorDto;
import ir.garm.cache.domain.dto.ResponseData;
import ir.garm.cache.service.CacheHandler;
import ir.garm.cache.service.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ir.garm.cache.domain.constant.Constants.CACHE_KEY_NAME;
import static ir.garm.cache.domain.constant.Constants.CACHE_TOPIC_NAME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.encodeBasicAuth;

@Slf4j
@Service
public class AccessTokenCacheService implements CacheHandler<AccessTokenDto>, ResponseHandler<AccessTokenDto> {
    private final ApplicationProperties props;
    private final WebClient webClient;
    private final KafkaTemplate<String, AccessTokenDto> template;

    public AccessTokenCacheService(ApplicationProperties props,
                                   @Qualifier("cacheWebClient") WebClient webClient,
                                   KafkaTemplate<String, AccessTokenDto> template) {
        this.props = props;
        this.webClient = webClient;
        this.template = template;
    }

    @Override
    public ResponseData<AccessTokenDto> generateToken(String... refresh_token) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        if (ArrayUtils.isNotEmpty(refresh_token)) {
            formData.add("grant_type", props.getAuthInfo().getGrant_refresh());
            formData.add("refresh_token", refresh_token[0]);
        } else {
            formData.add("grant_type", props.getAuthInfo().getGrant_password());
            formData.add("username", props.getAuthInfo().getUsername());
            formData.add("password", props.getAuthInfo().getPassword());
        }

        ClientResponse clientResponse = webClient.post().uri(props.getAuthInfo().getUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers -> headers.add(AUTHORIZATION, "Basic " + encodeBasicAuth(props.getAuthInfo().getClientId(),
                        props.getAuthInfo().getClientSecret(), StandardCharsets.UTF_8)))
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(ClientResponse.class)
                .block();

        return processResponse(clientResponse);
    }

    @Override
    public void doProcessException(ClientResponse clientResponse, HttpStatusCode status) {
        AccessTokenErrorDto errorDto = clientResponse.bodyToMono(AccessTokenErrorDto.class).block();
        if (errorDto != null) {
            log.error("error:{}\n message:{}\n", errorDto.getError(), errorDto.getDescription());
        }
    }

    @Override
    public AccessTokenDto doProcessResponse(ClientResponse clientResponse) {
        AccessTokenDto tokenDto = clientResponse.bodyToMono(AccessTokenDto.class).block();
        if (Objects.nonNull(tokenDto)) {
            setCreatedAt(tokenDto);
            template.send(CACHE_TOPIC_NAME, CACHE_KEY_NAME, tokenDto);
        }
        return tokenDto;
    }
}
