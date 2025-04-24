package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.util.permanentRedirect
import io.github.kingg22.api.vacunas.panama.util.toUri
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Deprecated(message = "For compatibility in v1. Redirect to specific controller in modules.")
@RestController
@RequestMapping(path = ["/public"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PublicResourcesController {
    @GetMapping("/distritos")
    suspend fun getDistritos() = permanentRedirect("/vacunacion/v1/direccion/distritos".toUri())

    @GetMapping("/provincias")
    suspend fun getProvincias() = permanentRedirect("/vacunacion/v1/direccion/provincias".toUri())

    @GetMapping("/sedes")
    suspend fun getSedes() = permanentRedirect("/vacunacion/v1/sedes".toUri())

    @GetMapping("/vacunas", "/vaccines")
    suspend fun getVacunas() = permanentRedirect("/vacunacion/v1/vaccines".toUri())

    @GetMapping("/roles")
    suspend fun getRoles() = permanentRedirect("/vacunacion/v1/roles".toUri())

    @GetMapping("/roles/permisos")
    suspend fun getPermisos() = permanentRedirect("/vacunacion/v1/roles/permisos".toUri())
}
