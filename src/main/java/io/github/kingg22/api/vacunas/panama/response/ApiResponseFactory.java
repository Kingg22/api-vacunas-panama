package io.github.kingg22.api.vacunas.panama.response;

import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseFactory {

    public IApiResponse<String, Serializable> createResponse() {
        return new ApiResponse();
    }
}
