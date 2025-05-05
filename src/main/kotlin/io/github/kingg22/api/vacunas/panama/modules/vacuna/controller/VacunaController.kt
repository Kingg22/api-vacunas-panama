package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/vaccines")
@Produces(MediaType.APPLICATION_JSON)
class VacunaController(private val vacunaService: VacunaService) {
    @GET
    @PermitAll
    suspend fun getVacunas(rc: RoutingContext) =
        createApiAndResponseEntity(rc, mapOf("vacunas" to vacunaService.getVacunasFabricante().toArrayList()))

    @Path("/create-dosis")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("DOCTOR", "ENFERMERA")
    suspend fun createDosis(@Valid insertDosisDto: InsertDosisDto, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        apiResponse.mergeContentResponse(vacunaService.createDosis(insertDosisDto))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(400)
        } else {
            apiResponse.addStatusCode(201)
        }
        return createResponseEntity(apiResponse, rc)
    }
}
