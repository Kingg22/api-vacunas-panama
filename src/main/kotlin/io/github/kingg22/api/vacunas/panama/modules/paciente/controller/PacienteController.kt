package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/patient"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PacienteController(private val pacienteService: PacienteService) {
    private val log = logger()

    @GetMapping
    suspend fun getPaciente(
        @AuthenticationPrincipal jwt: Jwt,
        request: ServerHttpRequest,
    ): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()
        val personaIdString = jwt.getClaimAsString("persona")
        check(personaIdString != null) { "Persona ID is null in JWT claims with ID: ${jwt.id}" }
        val idPersona = UUID.fromString(personaIdString)
        log.debug("Received a query of Paciente: {}", idPersona)
        val viewPacienteVacunaEnfermedad = pacienteService.getViewVacunaEnfermedad(idPersona).toArrayList()
        apiResponse.addData("view_vacuna_enfermedad", viewPacienteVacunaEnfermedad)
        if (viewPacienteVacunaEnfermedad.isEmpty()) {
            apiResponse.addError(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.NOT_FOUND)
                    message = "El paciente no tiene dosis registradas"
                },
            )
            apiResponse.addStatusCode(HttpStatus.NOT_FOUND)
        } else {
            apiResponse.addStatusCode(HttpStatus.OK)
        }
        return createResponseEntity(apiResponse, request)
    }
}
