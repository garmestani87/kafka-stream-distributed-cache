package ir.garm.cache.service;

import ir.garm.cache.domain.dto.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Objects;

public interface ResponseHandler<R> {
    @Slf4j
    final class LogHolder {
    }

    default ResponseData<R> processResponse(ClientResponse clientResponse) {
        ResponseData<R> response = new ResponseData<R>().setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        if (Objects.nonNull(clientResponse)) {
            HttpStatusCode httpStatus = clientResponse.statusCode();
            if (httpStatus != HttpStatus.OK) {
                tryException(clientResponse, httpStatus);
            } else {
                tryResponse(clientResponse, response);
            }
        }
        return response;
    }

    default void tryException(ClientResponse clientResponse, HttpStatusCode status) {
        try {
            doProcessException(clientResponse, status);
        } catch (Exception ex) {
            LogHolder.log.error("error:{}\n message:{}\n", status.value(), "exception occur when parsing error!");
        }
    }

    default void tryResponse(ClientResponse clientResponse, ResponseData<R> response) {
        try {
            response.setResponse(doProcessResponse(clientResponse));
            response.setStatus(HttpStatus.OK);
        } catch (Exception ex) {
            LogHolder.log.error("exception occur when parsing response!");
        }
    }

    void doProcessException(ClientResponse clientResponse, HttpStatusCode status);

    R doProcessResponse(ClientResponse clientResponse);
}
