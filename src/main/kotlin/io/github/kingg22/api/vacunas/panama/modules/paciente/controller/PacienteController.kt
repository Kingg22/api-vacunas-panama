package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.IPacienteService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping(path = ["/patient"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PacienteController(private val pacienteService: IPacienteService) {
    private val log = logger()

    @GetMapping
    fun getPaciente(@AuthenticationPrincipal jwt: Jwt, request: ServerHttpRequest): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        val idPersona = UUID.fromString(jwt.getClaimAsString("persona"))
        log.debug("Received a query of Paciente: {}", idPersona)
        val viewPacienteVacunaEnfermedadDtoList = ArrayList(
            pacienteService.getViewVacunaEnfermedad(idPersona),
        )
        apiResponse.addData("view_vacuna_enfermedad", viewPacienteVacunaEnfermedadDtoList)
        if (viewPacienteVacunaEnfermedadDtoList.isEmpty()) {
            apiResponse.addError(
                DefaultApiError(ApiResponseCode.NOT_FOUND, "El paciente no tiene dosis registradas"),
            )
            apiResponse.addStatusCode(HttpStatus.NOT_FOUND)
        } else {
            apiResponse.addStatusCode(HttpStatus.OK)
        }
        return sendResponse(apiResponse, request)
    }
}
