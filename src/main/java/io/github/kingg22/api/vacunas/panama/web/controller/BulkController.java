package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.service.IPacienteService;
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import io.github.kingg22.api.vacunas.panama.web.dto.RegisterUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/bulk", produces = MediaType.APPLICATION_JSON_VALUE)
public class BulkController {
    private final IUsuarioManagementService usuarioManagementService;
    private final IPacienteService pacienteService;

    @Transactional
    @PostMapping("/paciente-usuario-direccion")
    public ResponseEntity<ApiResponse> createPacienteUsuario(
            @NotNull @RequestBody @Valid PacienteDto pacienteDto, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        log.debug(pacienteDto.toString());
        var validateContent = this.pacienteService.validateCreatePacienteUsuario(pacienteDto);
        apiResponse.addErrors(validateContent.getErrors());
        apiResponse.addWarnings(validateContent.getWarnings());
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            return ApiResponseUtil.sendResponse(apiResponse, request);
        }
        var pacienteContent = this.pacienteService.createPaciente(pacienteDto);
        apiResponse.addWarnings(pacienteContent.getWarnings());
        apiResponse.addErrors(pacienteContent.getErrors());
        if (pacienteContent.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            return ApiResponseUtil.sendResponse(apiResponse, request);
        }
        var registerUser = new RegisterUser(
                pacienteDto.persona().usuario(),
                pacienteDto.persona().cedula(),
                pacienteDto.persona().pasaporte());
        var apiContentResponse = this.usuarioManagementService.createUser(registerUser);
        apiResponse.mergeContentResponse(apiContentResponse);
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }
}
