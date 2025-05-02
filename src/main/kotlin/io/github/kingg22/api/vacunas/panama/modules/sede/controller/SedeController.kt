package io.github.kingg22.api.vacunas.panama.modules.sede.controller

import io.github.kingg22.api.vacunas.panama.modules.sede.service.SedeService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/sedes")
@Produces(MediaType.APPLICATION_JSON)
class SedeController(private val sedeService: SedeService) {
    @GET
    suspend fun getSedes() =
        createApiAndResponseEntity(null, mapOf("sedes" to sedeService.getIdNombreSedes().toArrayList()))
}
