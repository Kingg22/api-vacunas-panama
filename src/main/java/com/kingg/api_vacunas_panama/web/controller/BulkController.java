package com.kingg.api_vacunas_panama.web.controller;

import com.kingg.api_vacunas_panama.service.PacienteService;
import com.kingg.api_vacunas_panama.service.UsuarioManagementService;
import com.kingg.api_vacunas_panama.util.ApiContentResponse;
import com.kingg.api_vacunas_panama.util.ApiResponse;
import com.kingg.api_vacunas_panama.util.ApiResponseUtil;
import com.kingg.api_vacunas_panama.util.IApiResponse;
import com.kingg.api_vacunas_panama.web.dto.PacienteDto;
import com.kingg.api_vacunas_panama.web.dto.RegisterUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @PostMapping("/paciente-usuario-direccion")
    public ResponseEntity<IApiResponse<String, Serializable>> createPacienteUsuario(@RequestBody @Valid PacienteDto pacienteDto,
                                                                                    ServletWebRequest request) {
        IApiResponse<String, Serializable> apiResponse = new ApiResponse();
        log.debug(pacienteDto.toString());
        ApiContentResponse validateContent = this.pacienteService.validateCreatePacienteUsuario(pacienteDto);
        apiResponse.addErrors(validateContent.getErrors());
        apiResponse.addWarnings(validateContent.getWarnings());
        if (!apiResponse.hasErrors()) {
            ApiContentResponse pacienteContent = this.pacienteService.createPaciente(pacienteDto);
            apiResponse.addWarnings(pacienteContent.getWarnings());
            apiResponse.addErrors(pacienteContent.getErrors());
            if (pacienteContent.hasErrors()) {
                apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            } else {
                RegisterUser registerUser = new RegisterUser(pacienteDto.getUsuario(), pacienteDto.getCedula(), pacienteDto.getPasaporte(), null);
                ApiContentResponse apiContentResponse = this.usuarioManagementService.createUser(registerUser);
                apiResponse.addWarnings(apiContentResponse.getWarnings());
                apiResponse.addErrors(apiContentResponse.getErrors());
                apiResponse.addData(apiContentResponse.getData());
                if (apiContentResponse.hasErrors()) {
                    apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
                } else {
                    apiResponse.addStatusCode(HttpStatus.CREATED);
                }
            }
        } else {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

}
