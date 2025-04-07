package io.github.kingg22.api.vacunas.panama.modules.sede.controller

import io.github.kingg22.api.vacunas.panama.modules.sede.service.SedeService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/sedes"], produces = [MediaType.APPLICATION_JSON_VALUE])
class SedeController(private val sedeService: SedeService) {
    @GetMapping
    fun getSedes(request: ServerHttpRequest) =
        createAndSendResponse(request, "sedes", sedeService.getIdNombreSedes().toArrayList())
}
