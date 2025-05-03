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
    suspend fun getDistritos() = permanentRedirect("/direccion/distritos")

    @GET
    @Path("/provincias")
    suspend fun getProvincias() = permanentRedirect("/direccion/provincias")

    @GET
    @Path("/sedes")
    suspend fun getSedes() = permanentRedirect("/sedes")

    @GET
    @Path("/vacunas")
    suspend fun getVacunas() = permanentRedirect("/vaccines")

    @GET
    @Path("/vaccines")
    suspend fun getVaccines() = permanentRedirect("/vaccines".toUri())

    @GET
    @Path("/roles")
    suspend fun getRoles() = permanentRedirect("/roles".toUri())

    @GET
    @Path("/roles/permisos")
    suspend fun getPermisos() = permanentRedirect("/roles/permisos".toUri())
}
