package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.service.IVacunaService;
import io.github.kingg22.api.vacunas.panama.web.dto.InsertDosisDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/vaccines/", produces = MediaType.APPLICATION_JSON_VALUE)
public class VacunaController {
    private final IVacunaService vacunaService;

    @PostMapping("/create-dosis")
    public ResponseEntity<ApiResponse> createDosis(
            @RequestBody @Valid InsertDosisDto insertDosisDto, ServletWebRequest servletWebRequest) {
        var apiResponse = ApiResponseFactory.createResponse();
        var response = vacunaService.createDosis(insertDosisDto);
        apiResponse.mergeContentResponse(response);
        if (response.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED);
        }
        return ApiResponseUtil.sendResponse(apiResponse, servletWebRequest);
    }
}
