package io.github.kingg22.api_vacunas_panama.exceptions;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Map;

@Component
public class MethodNotSupportedExceptionHandler implements ApiExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof HttpRequestMethodNotSupportedException;
    }

    @Override
    public ApiErrorResponse handle(Throwable exception) {
        HttpRequestMethodNotSupportedException methodException = (HttpRequestMethodNotSupportedException) exception;
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.toString(), exception.getMessage());
        response.addErrorProperty("cause", Map.of(HttpStatus.METHOD_NOT_ALLOWED.toString(), methodException.getMethod()));
        return response;
    }

}
