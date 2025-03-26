package io.github.kingg22.api.vacunas.panama.response;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiFieldError;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiGlobalError;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiParameterError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Utility class for handling and formatting API responses.
 * Provides helper methods to standardize metadata and create ResponseEntity objects.
 */
@Slf4j
public class ApiResponseUtil {
    private static final ApiResponseFactory apiResponseFactory = new ApiResponseFactory();

    private ApiResponseUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Adds metadata information to the provided API response.
     * @param apiResponse {@link IApiResponse} object to which metadata will be added.
     * @param servletWebRequest {@link ServletWebRequest} request data used to extract information.
     */
    public static void setMetadata(
            @NotNull IApiResponse<?, Serializable> apiResponse, ServletWebRequest servletWebRequest) {
        log.debug("servletWebRequest: {}", servletWebRequest);
        log.debug("locale: {}", servletWebRequest.getLocale());
        apiResponse.addMetadata("path", servletWebRequest.getRequest().getRequestURI());
        apiResponse.addMetadata("timestamp", Instant.now().toString());
    }

    /**
     * Creates a standardized HTTP response from the API response.
     * @param apiResponse {@link IApiResponse} object containing the response with status.
     * @param webRequest {@link ServletWebRequest} used to set metadata in the response.
     * @return A {@link ResponseEntity} with the status code and body set to the API response object.
     */
    public static ResponseEntity<IApiResponse<String, Serializable>> sendResponse(
            IApiResponse<String, Serializable> apiResponse, ServletWebRequest webRequest) {
        setMetadata(apiResponse, webRequest);
        log.debug(apiResponse.toString());
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    /**
     * Transforms an {@link ApiErrorResponse} from the error-handling library into a custom API response format.
     * Additionally, it hides internal errors in the response.
     * @param apiErrorResponse {@link ApiErrorResponse} object to be transformed.
     * @param request Additional request information, indicating the endpoint where the exception was thrown.
     * @return A new {@link IApiResponse} object.
     */
    public static IApiResponse<String, Serializable> transformApiErrorResponse(
            @NotNull ApiErrorResponse apiErrorResponse, Object request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        response.addStatus("code", apiErrorResponse.getHttpStatus().value());
        if (!apiErrorResponse.getMessage().contains("Dto")
                && !apiErrorResponse.getMessage().contains("api_vacunas_panama")) {
            response.addError(apiErrorResponse.getCode(), apiErrorResponse.getMessage());
        } else {
            response.addError(apiErrorResponse.getCode(), "Intente nuevamente");
        }
        for (ApiFieldError fieldError : apiErrorResponse.getFieldErrors()) {
            response.addError(fieldError.getCode(), fieldError.getProperty(), fieldError.getMessage());
        }
        for (ApiGlobalError globalError : apiErrorResponse.getGlobalErrors()) {
            response.addError(globalError.getCode(), globalError.getMessage());
        }
        for (ApiParameterError parameterError : apiErrorResponse.getParameterErrors()) {
            response.addError(parameterError.getCode(), parameterError.getParameter(), parameterError.getMessage());
        }
        switch (request) {
            case ServletWebRequest servletWebRequest -> setMetadata(response, servletWebRequest);
            case HttpServletRequest httpServletRequest -> setMetadata(
                    response, new ServletWebRequest(httpServletRequest));
            default -> response.addMetadata("timestamp", Instant.now().toString());
        }
        log.debug(response.toString());
        log.debug(
                "ErrorResponse(code: {}, message: {}, properties {})",
                apiErrorResponse.getCode(),
                apiErrorResponse.getMessage(),
                apiErrorResponse.getProperties());
        return response;
    }
}
