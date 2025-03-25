package io.github.kingg22.api_vacunas_panama.web.controller;

import io.github.kingg22.api_vacunas_panama.response.ApiResponseFactory;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseUtil;
import io.github.kingg22.api_vacunas_panama.response.IApiResponse;
import io.github.kingg22.api_vacunas_panama.service.IDireccionService;
import io.github.kingg22.api_vacunas_panama.service.ISedeService;
import io.github.kingg22.api_vacunas_panama.service.IUsuarioManagementService;
import io.github.kingg22.api_vacunas_panama.service.IVacunaService;
import io.github.kingg22.api_vacunas_panama.web.dto.DistritoDto;
import io.github.kingg22.api_vacunas_panama.web.dto.IdNombreDto;
import io.github.kingg22.api_vacunas_panama.web.dto.ProvinciaDto;
import io.github.kingg22.api_vacunas_panama.web.dto.UUIDNombreDto;
import io.github.kingg22.api_vacunas_panama.web.dto.VacunaFabricanteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.Serializable;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/public", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicResourcesController {
    private final IDireccionService direccionService;
    private final ISedeService sedeService;
    private final IVacunaService vacunaService;
    private final IUsuarioManagementService usuarioManagementService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping("/distritos")
    public ResponseEntity<IApiResponse<String, Serializable>> getDistritos(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<DistritoDto> distritoDtos = new ArrayList<>(direccionService.getDistritosDto());
        response.addData("distritos", distritoDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/provincias")
    public ResponseEntity<IApiResponse<String, Serializable>> getProvincias(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<ProvinciaDto> provinciaDtos = new ArrayList<>(direccionService.getProvinciasDto());
        response.addData("provincias", provinciaDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/sedes")
    public ResponseEntity<IApiResponse<String, Serializable>> getSedes(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<UUIDNombreDto> sedesNombreDtos = new ArrayList<>(sedeService.getIdNombreSedes());
        response.addData("sedes", sedesNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/vacunas")
    public ResponseEntity<IApiResponse<String, Serializable>> getVacunas(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<VacunaFabricanteDto> vacunaFabricanteDtos = new ArrayList<>(vacunaService.getVacunasFabricante());
        response.addData("vacunas", vacunaFabricanteDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles")
    public ResponseEntity<IApiResponse<String, Serializable>> getRoles(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<IdNombreDto> rolesNombreDtos = new ArrayList<>(usuarioManagementService.getIdNombreRoles());
        response.addData("roles", rolesNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles/permisos")
    public ResponseEntity<IApiResponse<String, Serializable>> getPermisos(ServletWebRequest request) {
        IApiResponse<String, Serializable> response = apiResponseFactory.createResponse();
        ArrayList<IdNombreDto> permisosNombreDtos = new ArrayList<>(usuarioManagementService.getIdNombrePermisos());
        response.addData("permisos", permisosNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

}
