package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.util.permanentRedirect
import io.github.kingg22.api.vacunas.panama.util.toUri
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Deprecated(message = "For compatibility in v1. Redirect to specific controller in modules.")
@Path("/public")
@Produces(MediaType.APPLICATION_JSON)
class PublicResourcesController {

    @GET
    @Path("/distritos")
    suspend fun getDistritos() = permanentRedirect("/vacunacion/v1/direccion/distritos")

    @GET
    @Path("/provincias")
    suspend fun getProvincias() = permanentRedirect("/vacunacion/v1/direccion/provincias")

    @GET
    @Path("/sedes")
    suspend fun getSedes() = permanentRedirect("/vacunacion/v1/sedes")

    @GET
    @Path("/vacunas")
    suspend fun getVacunas() = permanentRedirect("/vacunacion/v1/vaccines")

    @GET
    @Path("/vaccines")
    suspend fun getVaccines() = permanentRedirect("/vacunacion/v1/vaccines".toUri())

    @GET
    @Path("/roles")
    suspend fun getRoles() = permanentRedirect("/vacunacion/v1/roles".toUri())

    @GET
    @Path("/roles/permisos")
    suspend fun getPermisos() = permanentRedirect("/vacunacion/v1/roles/permisos".toUri())
}
