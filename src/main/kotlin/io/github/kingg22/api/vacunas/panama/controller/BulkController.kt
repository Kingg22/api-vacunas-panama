package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteInputDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** TODO to split in modules controllers */
@RestController
@RequestMapping(path = ["/bulk"], produces = [MediaType.APPLICATION_JSON_VALUE])
class BulkController(private val usuarioService: UsuarioService, private val pacienteService: PacienteService) {
    private val log = logger()

    @PostMapping("/paciente-usuario-direccion")
    suspend fun createPacienteUsuario(
        @RequestBody @Valid pacienteInputDto: PacienteInputDto,
        request: ServerHttpRequest,
    ): ResponseEntity<ActualApiResponse> {
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
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST.value())
            return createResponseEntity(apiResponse, request)
        }
        val registerUserDto = RegisterUserDto(
            pacienteDto.persona.usuario!!,
            pacienteDto.persona.cedula,
            pacienteDto.persona.pasaporte,
        )
        log.trace("RegisterUserDto: {}", registerUserDto.toString())
        val apiContentResponse = usuarioService.createUser(registerUserDto, null)
        apiResponse.mergeContentResponse(apiContentResponse)
        log.trace("CreateUser return: {}", apiContentResponse.toString())
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST.value())
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED.value())
        }
        return createResponseEntity(apiResponse, request)
    }
}
