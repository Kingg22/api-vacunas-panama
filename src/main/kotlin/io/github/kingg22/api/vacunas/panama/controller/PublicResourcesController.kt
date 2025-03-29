package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.modules.direccion.service.IDireccionService
import io.github.kingg22.api.vacunas.panama.modules.sede.service.ISedeService
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.IUsuarioManagementService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.IVacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** TODO move to specific module controller in default '/' get */
@RestController
@RequestMapping(path = ["/vacunacion/v1/public"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PublicResourcesController(
    private val direccionService: IDireccionService,
    private val sedeService: ISedeService,
    private val vacunaService: IVacunaService,
    private val usuarioManagementService: IUsuarioManagementService,
) {
    @GetMapping("/distritos")
    fun getDistritos(request: ServerHttpRequest) =
        createAndSendResponse(request, "distritos", direccionService.distritosDto.toArrayList())

    @GetMapping("/provincias")
    fun getProvincias(request: ServerHttpRequest) =
        createAndSendResponse(request, "provincias", direccionService.provinciasDto.toArrayList())

    @GetMapping("/sedes")
    fun getSedes(request: ServerHttpRequest) =
        createAndSendResponse(request, "sedes", sedeService.idNombreSedes.toArrayList())

    @GetMapping("/vacunas")
    fun getVacunas(request: ServerHttpRequest) =
        createAndSendResponse(request, "vacunas", vacunaService.vacunasFabricante.toArrayList())

    @GetMapping("/roles")
    fun getRoles(request: ServerHttpRequest) =
        createAndSendResponse(request, "roles", usuarioManagementService.idNombreRoles.toArrayList())

    @GetMapping("/roles/permisos")
    fun getPermisos(request: ServerHttpRequest) =
        createAndSendResponse(request, "permisos", usuarioManagementService.idNombrePermisos.toArrayList())
}
