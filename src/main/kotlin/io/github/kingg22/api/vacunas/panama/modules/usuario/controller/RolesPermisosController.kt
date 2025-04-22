package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RolPermisoService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
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
    suspend fun getRoles(request: ServerHttpRequest) =
        createApiAndResponseEntity(request, mapOf("roles" to rolesPermisoService.getIdNombreRoles().toArrayList()))

    @GetMapping("/permisos")
    suspend fun getPermisos(request: ServerHttpRequest) = createApiAndResponseEntity(
        request,
        mapOf("permisos" to rolesPermisoService.getIdNombrePermisos().toArrayList()),
    )
}
