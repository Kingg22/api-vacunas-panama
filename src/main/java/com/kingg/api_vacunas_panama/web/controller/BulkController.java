package com.kingg.api_vacunas_panama.web.controller;

import com.kingg.api_vacunas_panama.service.PacienteService;
import com.kingg.api_vacunas_panama.service.UsuarioManagementService;
import com.kingg.api_vacunas_panama.util.*;
import com.kingg.api_vacunas_panama.web.dto.PacienteDto;
import com.kingg.api_vacunas_panama.web.dto.UsuarioDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.Serializable;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/bulk", produces = MediaType.APPLICATION_JSON_VALUE)
public class BulkController {
    private final UsuarioManagementService usuarioManagementService;
    private final PacienteService pacienteService;

    @PostMapping("/create/paciente-usuario-direccion")
    public ResponseEntity<IApiResponse<String, Serializable>> createPacienteUsuario(@RequestBody @Valid PacienteDto pacienteDto,
                                                                                    ServletWebRequest request) {
        ApiResponse temporal = new ApiResponse();
        IApiResponse<String, Serializable> apiResponse = new ApiResponse();
        log.debug(pacienteDto.toString());
        if (pacienteDto.getUsuario() == null) {
            temporal.addError(ApiResponseCode.MISSING_INFORMATION, "Esta función necesita el usuario para el paciente");
        }
        if (pacienteDto.getUsuario() != null && pacienteDto.getUsuario().id() == null && pacienteDto.getUsuario().roles().stream().anyMatch(rolDto -> rolDto.nombre() != null)) {
            temporal.addError(ApiResponseCode.NON_IDEMPOTENCE, "roles[]", "Utilice ID para el rol Paciente en esta función");
        }
        if (pacienteDto.getUsuario() != null && pacienteDto.getUsuario().roles().stream().anyMatch(rolDto -> rolDto.id() != null && !RolesEnum.getByPriority(rolDto.id()).equals(RolesEnum.PACIENTE))) {
            temporal.addError(ApiResponseCode.VALIDATION_FAILED, "roles[]", "Esta función es solo para pacientes, utilice otra operación");
        }
        if (pacienteDto.getSexo().toString().equalsIgnoreCase("X")) {
            temporal.addWarning(ApiResponseCode.DEPRECATION_WARNING, "sexo", "Pacientes no deben tener sexo no definido. Reglas de vacunación no se podrán aplicar");
        }
        if (pacienteDto.getDireccion() != null && pacienteDto.getDireccion().id() == null) {
            temporal.addWarning(ApiResponseCode.NON_IDEMPOTENCE, "direccion", "Debe trabajar con ID");
        }
        if (pacienteDto.getUsuario() != null && pacienteDto.getUsuario().createdAt() != null &&
                pacienteDto.getCreatedAt() != null && !pacienteDto.getCreatedAt().isEqual(pacienteDto.getUsuario().createdAt())) {
            temporal.addError(ApiResponseCode.VALIDATION_FAILED, "created_at", "created_at de Paciente y Usuario deben ser las mismas o null");
        }

        if (!temporal.hasErrors()) {
            try {
                PacienteDto paciente = this.pacienteService.createPaciente(pacienteDto);
                UsuarioDto temp = pacienteDto.getUsuario();
                UsuarioDto usuarioDto = new UsuarioDto(temp.id(), temp.username(), temp.password(), temp.createdAt(), null, null, temp.roles(), paciente.getCedula(), paciente.getPasaporte(), null);
                apiResponse = this.usuarioManagementService.createUser(usuarioDto);
                if (temporal.hasWarnings()) {
                    apiResponse.addWarnings(temporal.getWarnings());
                }
            } catch (RuntimeException exception) {
                log.error("Error en createPacienteUsuario: ".concat(exception.getMessage()), exception);
                apiResponse.addError("BAD_REQUEST", exception.getMessage());
                apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            }
        } else {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

}
