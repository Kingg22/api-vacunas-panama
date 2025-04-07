package io.github.kingg22.api.vacunas.panama.modules.direccion.controller

import io.github.kingg22.api.vacunas.panama.modules.direccion.service.DireccionService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
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
    fun getDistritos(request: ServerHttpRequest) =
        createAndSendResponse(request, "distritos", direccionService.getDistritosDto().toArrayList())

    @GetMapping("/provincias")
    fun getProvincias(request: ServerHttpRequest) =
        createAndSendResponse(request, "provincias", direccionService.getProvinciasDto().toArrayList())
}
