package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
import io.github.kingg22.api.vacunas.panama.service.IDireccionService
import io.github.kingg22.api.vacunas.panama.service.ISedeService
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService
import io.github.kingg22.api.vacunas.panama.service.IVacunaService
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest

@RestController
@RequestMapping(path = ["/vacunacion/v1/public"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PublicResourcesController(
    private val direccionService: IDireccionService,
    private val sedeService: ISedeService,
    private val vacunaService: IVacunaService,
    private val usuarioManagementService: IUsuarioManagementService,
) {
    @GetMapping("/distritos")
    fun getDistritos(request: ServletWebRequest) =
        createAndSendResponse(request, "distritos", direccionService.distritosDto.toArrayList())

    @GetMapping("/provincias")
    fun getProvincias(request: ServletWebRequest) =
        createAndSendResponse(request, "provincias", direccionService.provinciasDto.toArrayList())

    @GetMapping("/sedes")
    fun getSedes(request: ServletWebRequest) =
        createAndSendResponse(request, "sedes", sedeService.idNombreSedes.toArrayList())

    @GetMapping("/vacunas")
    fun getVacunas(request: ServletWebRequest) =
        createAndSendResponse(request, "vacunas", vacunaService.vacunasFabricante.toArrayList())

    @GetMapping("/roles")
    fun getRoles(request: ServletWebRequest) =
        createAndSendResponse(request, "roles", usuarioManagementService.idNombreRoles.toArrayList())

    @GetMapping("/roles/permisos")
    fun getPermisos(request: ServletWebRequest) =
        createAndSendResponse(request, "permisos", usuarioManagementService.idNombrePermisos.toArrayList())
}
