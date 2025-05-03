package io.github.kingg22.api.vacunas.panama.modules.direccion.controller

import io.github.kingg22.api.vacunas.panama.modules.direccion.service.DireccionService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/direccion")
@Produces(MediaType.APPLICATION_JSON)
class DireccionController(private val direccionService: DireccionService) {
    @GET
    @Path("/distritos")
    suspend fun getDistritos(rc: RoutingContext) =
        createApiAndResponseEntity(rc, mapOf("distritos" to direccionService.getDistritosDto().toArrayList()))

    @GET
    @Path("/provincias")
    suspend fun getProvincias(rc: RoutingContext) =
        createApiAndResponseEntity(rc, mapOf("provincias" to direccionService.getProvinciasDto().toArrayList()))
}
