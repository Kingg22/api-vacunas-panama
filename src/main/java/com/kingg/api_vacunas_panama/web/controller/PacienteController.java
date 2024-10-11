package com.kingg.api_vacunas_panama.web.controller;

import com.kingg.api_vacunas_panama.service.PacienteService;
import com.kingg.api_vacunas_panama.util.ApiResponse;
import com.kingg.api_vacunas_panama.util.ApiResponseCode;
import com.kingg.api_vacunas_panama.util.ApiResponseUtil;
import com.kingg.api_vacunas_panama.util.IApiResponse;
import com.kingg.api_vacunas_panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/patient", produces = MediaType.APPLICATION_JSON_VALUE)
public class PacienteController {
    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<Object> getPaciente(@AuthenticationPrincipal Jwt jwt, ServletWebRequest request) {
        IApiResponse<?, Object> apiResponse = new ApiResponse();
        apiResponse.addStatusCode(HttpStatus.OK);
        UUID idPersona = UUID.fromString(jwt.getClaimAsString("persona"));
        log.debug("Received a query of Paciente: {}", idPersona);
        List<ViewPacienteVacunaEnfermedadDto> viewPacienteVacunaEnfermedadDtoList = this.pacienteService.getViewVacunaEnfermedad(idPersona);
        apiResponse.addData("view_vacuna_enfermedad", viewPacienteVacunaEnfermedadDtoList);
        if (viewPacienteVacunaEnfermedadDtoList.isEmpty()) {
            apiResponse.addError(ApiResponseCode.NOT_FOUND, "El paciente no tiene dosis registradas");
            apiResponse.addStatusCode(HttpStatus.NOT_FOUND);
        } else {
            apiResponse.addStatusCode(HttpStatus.OK);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

}
