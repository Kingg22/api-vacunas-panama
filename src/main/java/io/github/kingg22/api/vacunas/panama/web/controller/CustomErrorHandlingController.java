package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ErrorHandlingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class CustomErrorHandlingController {
    private final ErrorHandlingFacade errorHandlingFacade;

    @ExceptionHandler
    public ResponseEntity<ApiResponse> handleException(Throwable exception, ServletWebRequest webRequest) {
        var errorResponse = errorHandlingFacade.handle(exception);
        return ResponseEntity.status(errorResponse.getHttpStatus())
                .body(ApiResponseUtil.transformApiErrorResponse(errorResponse, webRequest));
    }
}
