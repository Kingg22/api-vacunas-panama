package io.github.kingg22.api_vacunas_panama.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseUtil;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorCodeMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorMessageMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.HttpStatusMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    private final HttpStatusMapper httpStatusMapper;
    private final ErrorCodeMapper errorCodeMapper;
    private final ErrorMessageMapper errorMessageMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiErrorResponse errorResponse = creteResponse(authException);

        response.setStatus(errorResponse.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponseUtil.transformApiErrorResponse(errorResponse, request)));
    }

    private ApiErrorResponse creteResponse(AuthenticationException exception) {
        HttpStatusCode httpStatusCode = httpStatusMapper.getHttpStatus(exception, HttpStatus.UNAUTHORIZED);
        String code = errorCodeMapper.getErrorCode(exception);
        String message = errorMessageMapper.getErrorMessage(exception);
        return new ApiErrorResponse(httpStatusCode, code, message);
    }
}
