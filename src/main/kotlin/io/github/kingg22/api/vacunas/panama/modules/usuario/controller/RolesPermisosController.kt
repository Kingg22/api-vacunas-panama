package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RolPermisoService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
class RolesPermisosController(private val rolesPermisoService: RolPermisoService) {
    @GET
    suspend fun getRoles(rc: RoutingContext) =
        createApiAndResponseEntity(rc, mapOf("roles" to rolesPermisoService.getIdNombreRoles().toArrayList()))

    @Path("/permisos")
    @GET
    suspend fun getPermisos(rc: RoutingContext) = createApiAndResponseEntity(
        rc,
        mapOf("permisos" to rolesPermisoService.getIdNombrePermisos().toArrayList()),
    )
}
