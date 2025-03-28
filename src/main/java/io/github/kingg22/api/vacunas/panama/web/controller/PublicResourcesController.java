package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.service.IDireccionService;
import io.github.kingg22.api.vacunas.panama.service.ISedeService;
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService;
import io.github.kingg22.api.vacunas.panama.service.IVacunaService;
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/public", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicResourcesController {
    private final IDireccionService direccionService;
    private final ISedeService sedeService;
    private final IVacunaService vacunaService;
    private final IUsuarioManagementService usuarioManagementService;

    @GetMapping("/distritos")
    public ResponseEntity<ApiResponse> getDistritos(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var distritoDtos = new ArrayList<>(direccionService.getDistritosDto());
        response.addData("distritos", distritoDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/provincias")
    public ResponseEntity<ApiResponse> getProvincias(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var provinciaDtos = new ArrayList<>(direccionService.getProvinciasDto());
        response.addData("provincias", provinciaDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/sedes")
    public ResponseEntity<ApiResponse> getSedes(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var sedesNombreDtos = new ArrayList<>(sedeService.getIdNombreSedes());
        response.addData("sedes", sedesNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/vacunas")
    public ResponseEntity<ApiResponse> getVacunas(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var vacunaFabricanteDtos = new ArrayList<>(vacunaService.getVacunasFabricante());
        response.addData("vacunas", vacunaFabricanteDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getRoles(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var rolesNombreDtos = new ArrayList<>(usuarioManagementService.getIdNombreRoles());
        response.addData("roles", rolesNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }

    @GetMapping("/roles/permisos")
    public ResponseEntity<ApiResponse> getPermisos(ServletWebRequest request) {
        var response = ApiResponseFactory.createResponse();
        var permisosNombreDtos = new ArrayList<>(usuarioManagementService.getIdNombrePermisos());
        response.addData("permisos", permisosNombreDtos);
        response.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(response, request);
    }
}
