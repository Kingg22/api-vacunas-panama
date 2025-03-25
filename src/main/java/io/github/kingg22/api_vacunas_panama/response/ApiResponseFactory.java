package io.github.kingg22.api_vacunas_panama.response;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ApiResponseFactory {

    public IApiResponse<String, Serializable> createResponse() {
        return new ApiResponse();
    }

}
