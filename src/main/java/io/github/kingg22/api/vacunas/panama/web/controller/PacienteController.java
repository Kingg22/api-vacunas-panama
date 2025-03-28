package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import io.github.kingg22.api.vacunas.panama.service.IPacienteService;
import java.util.ArrayList;
import java.util.UUID;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/patient", produces = MediaType.APPLICATION_JSON_VALUE)
public class PacienteController {
    private final IPacienteService pacienteService;

    @GetMapping
    public ResponseEntity<ApiResponse> getPaciente(@AuthenticationPrincipal Jwt jwt, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        apiResponse.addStatusCode(HttpStatus.OK);
        var idPersona = UUID.fromString(jwt.getClaimAsString("persona"));
        log.debug("Received a query of Paciente: {}", idPersona);
        var viewPacienteVacunaEnfermedadDtoList =
            new ArrayList<>(this.pacienteService.getViewVacunaEnfermedad(idPersona));
        apiResponse.addData("view_vacuna_enfermedad", viewPacienteVacunaEnfermedadDtoList);
        if (viewPacienteVacunaEnfermedadDtoList.isEmpty()) {
            apiResponse.addError(new DefaultApiError(
                ApiResponseCode.NOT_FOUND,
                "El paciente no tiene dosis registradas"));
            apiResponse.addStatusCode(HttpStatus.NOT_FOUND);
        } else {
            apiResponse.addStatusCode(HttpStatus.OK);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }
}
