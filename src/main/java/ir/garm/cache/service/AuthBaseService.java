package ir.garm.cache.service;

import ir.garm.cache.service.impl.AccessTokenService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

public interface AuthBaseService {

    default String setAuthorization(AccessTokenService accessTokenService) {

        return "Bearer " + (accessTokenService.getAccessToken().getStatus() == HttpStatus.OK
                ? accessTokenService.getAccessToken().getData().getAccess_token()
                : Strings.EMPTY);

    }

}
