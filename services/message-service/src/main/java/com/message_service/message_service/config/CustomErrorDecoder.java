package com.message_service.message_service.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign error: {} - {}", response.status(), response.reason());

        if (response.status() == 404) {
            return new RuntimeException("User not found");
        }

        if (response.status() == 400) {
            return new RuntimeException("Bad request to user service");
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}