package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID

@Path("/patient")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@RolesAllowed("PACIENTE_READ")
class PacienteController(private val pacienteService: PacienteService) {
    private val log = logger()

    @Inject
    lateinit var jwt: JsonWebToken

    @GET
    suspend fun getPaciente(rc: RoutingContext): Response {
        val apiResponse = createResponse()
        val personaIdString = checkNotNull(jwt.getClaim<String>("persona")) {
            "Claim persona is null in jwt ${jwt.subject}"
        }
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
        return createResponseEntity(apiResponse, rc)
    }
}
