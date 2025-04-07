package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioManagementService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.util.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/** TODO to split in modules controllers */
@RestController
@RequestMapping(path = ["/bulk"], produces = [MediaType.APPLICATION_JSON_VALUE])
class BulkController(
    private val usuarioManagementService: UsuarioManagementService,
    private val pacienteService: PacienteService,
) {
    private val log = logger()

    @Transactional
    @PostMapping("/paciente-usuario-direccion")
    fun createPacienteUsuario(
        @RequestBody @Valid pacienteDto: PacienteDto,
        request: ServerHttpRequest,
    ): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        log.debug(pacienteDto.toString())
        val validateContent = pacienteService.validateCreatePacienteUsuario(pacienteDto)
        apiResponse.addErrors(validateContent.errors)
        apiResponse.addWarnings(validateContent.warnings)
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
            return sendResponse(apiResponse, request)
        }
        val pacienteContent = pacienteService.createPaciente(pacienteDto)
        apiResponse.addWarnings(pacienteContent.warnings)
        apiResponse.addErrors(pacienteContent.errors)
        if (pacienteContent.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
            return sendResponse(apiResponse, request)
        }
        val registerUserDto = RegisterUserDto(
            pacienteDto.persona.usuario!!,
            pacienteDto.persona.cedula,
            pacienteDto.persona.pasaporte,
        )
        val apiContentResponse = usuarioManagementService.createUser(registerUserDto)
        apiResponse.mergeContentResponse(apiContentResponse)
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED)
        }
        return sendResponse(apiResponse, request)
    }
}
