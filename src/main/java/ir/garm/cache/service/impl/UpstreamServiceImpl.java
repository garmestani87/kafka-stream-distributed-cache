package ir.garm.cache.service.impl;

import ir.garm.cache.config.ApplicationProperties;
import ir.garm.cache.domain.dto.ResponseData;
import ir.garm.cache.service.BaseUpstreamService;
import ir.garm.cache.service.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.UrlBase64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class UpstreamServiceImpl implements BaseUpstreamService<String, String>, ResponseHandler<String> {

    private final WebClient webClient;
    private final ApplicationProperties props;
    private final AccessTokenService accessTokenService;

    public UpstreamServiceImpl(ApplicationProperties props,
                               @Qualifier("cacheWebClient") WebClient webClient,
                               AccessTokenService accessTokenService) {
        this.webClient = webClient;
        this.props = props;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public ResponseData<String> call(String body) {
        ClientResponse clientResponse = webClient.get().uri(builder -> builder.path(props.getUpstreamInfo().getUri())
                        .queryParam("encoding", props.getUpstreamInfo().getEncoding())
                        .queryParam("data", new String(Base64.encode(body.getBytes())))
                        .build())
                .headers(headers -> headers.add(AUTHORIZATION, setAuthorization(accessTokenService)))
                .retrieve()
                .bodyToMono(ClientResponse.class)
                .block();

        return processResponse(clientResponse);
    }

    @Override
    public void doProcessException(ClientResponse clientResponse, HttpStatusCode httpStatus) {
        log.error("error:{}\n message:{}\n", httpStatus.value(), "error occur when signing response!");
    }

    @Override
    public String doProcessResponse(ClientResponse clientResponse) {
        ByteArrayResource result = clientResponse.bodyToMono(ByteArrayResource.class).block();
        return Objects.nonNull(result)
                ? new String(UrlBase64.encode(result.getByteArray()))
                : null;
    }
}
