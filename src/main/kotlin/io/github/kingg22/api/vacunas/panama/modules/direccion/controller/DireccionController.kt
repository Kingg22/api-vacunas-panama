package io.github.kingg22.api.vacunas.panama.modules.direccion.controller

import io.github.kingg22.api.vacunas.panama.modules.direccion.service.DireccionService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/direccion"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DireccionController(@Autowired private val direccionService: DireccionService) {
    @GetMapping("/distritos")
    suspend fun getDistritos(request: ServerHttpRequest) =
        createApiAndResponseEntity(request, mapOf("distritos" to direccionService.getDistritosDto().toArrayList()))

    @GetMapping("/provincias")
    suspend fun getProvincias(request: ServerHttpRequest) =
        createApiAndResponseEntity(request, mapOf("provincias" to direccionService.getProvinciasDto().toArrayList()))
}
