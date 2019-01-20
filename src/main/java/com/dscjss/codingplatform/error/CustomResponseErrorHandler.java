package com.dscjss.codingplatform.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        HttpStatus httpStatus = clientHttpResponse.getStatusCode();
        return httpStatus.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        logger.error("Response error: {} {}", clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusText());
    }
}
