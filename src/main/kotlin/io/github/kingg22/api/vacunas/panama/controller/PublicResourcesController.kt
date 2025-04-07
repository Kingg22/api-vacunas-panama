package io.github.kingg22.api.vacunas.panama.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@Deprecated(message = "For compatibility in v1. Redirect to specific controller in modules.")
@RestController
@RequestMapping(path = ["/public"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PublicResourcesController {
    @GetMapping("/distritos")
    fun getDistritos() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/direccion/distritos"))
            .build<Void>(),
    )

    @GetMapping("/provincias")
    fun getProvincias() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/direccion/provincias"))
            .build<Void>(),
    )

    @GetMapping("/sedes")
    fun getSedes() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/sedes")).build<Void>(),
    )

    @GetMapping("/vacunas", "/vaccines")
    fun getVacunas() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/vaccines"))
            .build<Void>(),
    )

    @GetMapping("/roles")
    fun getRoles() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/roles")).build<Void>(),
    )

    @GetMapping("/roles/permisos")
    fun getPermisos() = Mono.just(
        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/vacunacion/v1/roles/permisos"))
            .build<Void>(),
    )
}
