package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RolPermisoService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/roles"], produces = [MediaType.APPLICATION_JSON_VALUE])
class RolesPermisosController(private val rolesPermisoService: RolPermisoService) {
    @GetMapping
    fun getRoles(request: ServerHttpRequest) =
        createAndSendResponse(request, "roles", rolesPermisoService.getIdNombreRoles().toArrayList())

    @GetMapping("/permisos")
    fun getPermisos(request: ServerHttpRequest) =
        createAndSendResponse(request, "permisos", rolesPermisoService.getIdNombrePermisos().toArrayList())
}
