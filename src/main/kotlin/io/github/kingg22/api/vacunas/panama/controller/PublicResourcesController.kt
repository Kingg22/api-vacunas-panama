package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.util.toUri
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

@Deprecated(message = "For compatibility in v1. Redirect to specific controller in modules.")
@RestController
@RequestMapping(path = ["/public"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PublicResourcesController {
    @GetMapping("/distritos")
    suspend fun getDistritos() =
        ServerResponse.permanentRedirect("/vacunacion/v1/direccion/distritos".toUri()).buildAndAwait()

    @GetMapping("/provincias")
    suspend fun getProvincias() =
        ServerResponse.permanentRedirect("/vacunacion/v1/direccion/provincias".toUri()).buildAndAwait()

    @GetMapping("/sedes")
    suspend fun getSedes() = ServerResponse.permanentRedirect("/vacunacion/v1/sedes".toUri()).buildAndAwait()

    @GetMapping("/vacunas", "/vaccines")
    suspend fun getVacunas() = ServerResponse.permanentRedirect("/vacunacion/v1/vaccines".toUri()).buildAndAwait()

    @GetMapping("/roles")
    suspend fun getRoles() = ServerResponse.permanentRedirect("/vacunacion/v1/roles".toUri()).buildAndAwait()

    @GetMapping("/roles/permisos")
    suspend fun getPermisos() =
        ServerResponse.permanentRedirect("/vacunacion/v1/roles/permisos".toUri()).buildAndAwait()
}
