package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.UUID

@Path("/patient")
@Produces(MediaType.APPLICATION_JSON)
class PacienteController(private val pacienteService: PacienteService) {
    private val log = logger()

    @GET
    suspend fun getPaciente(): Response {
        // TODO add authentication principal or something else
        val jwt = object {
            val id: String = "1"
        }
        val apiResponse = createResponse()
        val personaIdString: String? = null
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
            apiResponse.addStatusCode(404)
        } else {
            apiResponse.addStatusCode(200)
        }
        return createResponseEntity(apiResponse)
    }
}
