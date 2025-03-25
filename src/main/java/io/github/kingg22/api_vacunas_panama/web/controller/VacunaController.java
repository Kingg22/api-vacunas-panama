package io.github.kingg22.api_vacunas_panama.web.controller;

import io.github.kingg22.api_vacunas_panama.response.ApiResponseFactory;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseUtil;
import io.github.kingg22.api_vacunas_panama.response.IApiResponse;
import io.github.kingg22.api_vacunas_panama.service.IVacunaService;
import io.github.kingg22.api_vacunas_panama.web.dto.InsertDosisDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.Serializable;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/vaccines/", produces = MediaType.APPLICATION_JSON_VALUE)
public class VacunaController {
    private final IVacunaService vacunaService;
    private final ApiResponseFactory apiResponseFactory;

    @PostMapping("/create-dosis")
    public ResponseEntity<IApiResponse<String, Serializable>> createDosis(@RequestBody @Valid InsertDosisDto insertDosisDto,
                                                                          ServletWebRequest servletWebRequest) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        var response = vacunaService.createDosis(insertDosisDto);
        apiResponse.addData(response.getData());
        apiResponse.addErrors(response.getErrors());
        apiResponse.addWarnings(response.getWarnings());
        if (response.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED);
        }
        return ApiResponseUtil.sendResponse(apiResponse, servletWebRequest);
    }

}
