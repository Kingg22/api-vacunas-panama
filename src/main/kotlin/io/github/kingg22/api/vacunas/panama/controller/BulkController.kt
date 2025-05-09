package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteInputDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/** TODO to split in modules controllers */
@Path("/bulk")
@Produces(MediaType.APPLICATION_JSON)
class BulkController(private val usuarioService: UsuarioService, private val pacienteService: PacienteService) {
    private val log = logger()

    @POST
    @Path("/paciente-usuario-direccion")
    suspend fun createPacienteUsuario(@Valid pacienteInputDto: PacienteInputDto, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        log.debug("Received a request to create a new Paciente, Dirección and User: {}", pacienteInputDto.toString())
        val pacienteDto = pacienteInputDto.toPacienteDto()
        log.debug(pacienteDto.toString())
        val pacienteContent = pacienteService.createPaciente(pacienteDto)
        log.trace(pacienteContent.toString())
        apiResponse.addWarnings(pacienteContent.warnings)
        apiResponse.addErrors(pacienteContent.errors)
        if (pacienteContent.hasErrors()) {
            log.trace("CreatePaciente return errors: {}", pacienteContent.errors)
            apiResponse.addStatusCode(400)
            return createResponseEntity(apiResponse, rc)
        }
        val registerUserDto = RegisterUserDto(
            pacienteDto.persona.usuario!!,
            pacienteDto.persona.cedula,
            pacienteDto.persona.pasaporte,
        )
        log.trace("RegisterUserDto: {}", registerUserDto.toString())
        // TODO add authentication verify of scopes
        val apiContentResponse = usuarioService.createUser(registerUserDto)
        apiResponse.mergeContentResponse(apiContentResponse)
        log.trace("CreateUser return: {}", apiContentResponse.toString())
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(400)
        } else {
            apiResponse.addStatusCode(201)
        }
        return createResponseEntity(apiResponse, rc)
    }
}
