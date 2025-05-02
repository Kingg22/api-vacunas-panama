package io.github.kingg22.api.vacunas.panama.modules.paciente.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.direccion.service.DireccionService
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.toPacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.persistence.PacientePersistenceService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.returnIfErrors
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil
import io.github.kingg22.api.vacunas.panama.util.logger
import jakarta.enterprise.context.ApplicationScoped
import org.springframework.cache.annotation.Cacheable
import java.util.UUID

@ApplicationScoped
class PacienteServiceImpl(
    private val pacientePersistenceService: PacientePersistenceService,
    private val direccionService: DireccionService,
) : PacienteService {
    private val log = logger()

    override suspend fun createPaciente(pacienteDto: PacienteDto): ActualApiResponse {
        val response = createContentResponse()
        response.addErrors(validatePacienteExist(pacienteDto))
        response.addErrors(validateCreatePaciente(pacienteDto))
        response.mergeContentResponse(validateCreatePacienteUsuario(pacienteDto))

        response.returnIfErrors()?.let { return it as ActualApiResponse }

        val direccion = pacienteDto.persona.direccion.let {
            direccionService.getDireccionByDto(it)
                ?: direccionService.createDireccion(it)
        }

        pacienteDto.persona.usuario?.let {
            log.warn(
                "createPaciente: Paciente have usuario, but this will be ignored. Please use updatePaciente instead. Usuario: {}",
                it,
            )
        }

        val paciente = pacientePersistenceService.savePaciente(
            pacienteDto.copy(
                updatedAt = null,
                persona = pacienteDto.persona.copy(
                    id = null,
                    direccion = direccion,
                    estado = pacienteDto.persona.estado ?: "ACTIVO",
                    usuario = null,
                ),
            ),
        )

        response.addData("paciente", paciente)
        return response
    }

    override suspend fun getPacienteDtoById(id: UUID) = pacientePersistenceService.findPacienteById(id)?.toPacienteDto()

    override suspend fun getPacienteById(idPaciente: UUID) =
        pacientePersistenceService.findPacienteById(idPaciente)?.toPacienteDto()?.toPacienteModel()

    @Cacheable(cacheNames = [CacheDuration.CACHE_VALUE], key = "'view_vacuna_enfermedad'.concat(#id)")
    override suspend fun getViewVacunaEnfermedad(id: UUID) =
        pacientePersistenceService.findAllFromViewVacunaEnfermedad(id)

    private suspend fun validatePacienteExist(pacienteDto: PacienteDto) = buildList {
        val persona = pacienteDto.persona

        persona.cedula?.let {
            val formatted = FormatterUtil.formatCedula(it)
            pacienteDto.copy(persona = persona.copy(cedula = formatted))
            if (pacientePersistenceService.findByPersonaCedula(formatted) != null) {
                add(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.VALIDATION_FAILED)
                        withProperty("cedula")
                        withMessage("Ya existe un paciente con esta cédula")
                    },
                )
            }
        }

        if (!persona.pasaporte.isNullOrBlank() &&
            pacientePersistenceService.findByPersonaPasaporte(persona.pasaporte) != null
        ) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("pasaporte")
                    withMessage("Ya existe un paciente con este pasaporte")
                },
            )
        }

        if (!pacienteDto.identificacionTemporal.isNullOrBlank() &&
            pacientePersistenceService.findByIdentificacionTemporal(pacienteDto.identificacionTemporal) != null
        ) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("identificacionTemporal")
                    withMessage("Ya existe un paciente con esta identificación temporal")
                },
            )
        }

        if (!persona.correo.isNullOrBlank() && pacientePersistenceService.findByPersonaCorreo(persona.correo) != null) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("correo")
                    withMessage("Ya existe un paciente con este correo")
                },
            )
        }

        if (!persona.telefono.isNullOrBlank() &&
            pacientePersistenceService.findByPersonaTelefono(persona.telefono) != null
        ) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("telefono")
                    withMessage("Ya existe un paciente con este teléfono")
                },
            )
        }
    }

    private suspend fun validateCreatePaciente(pacienteDto: PacienteDto) = buildList {
        val persona = pacienteDto.persona

        if (persona.nombre.isNullOrBlank() && persona.nombre2.isNullOrBlank()) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("nombre")
                    withMessage("El nombre del paciente es obligatorio")
                },
            )
        }

        if (persona.apellido1.isNullOrBlank() && persona.apellido2.isNullOrBlank()) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("apellido")
                    withMessage("El apellido del paciente es obligatorio")
                },
            )
        }

        if (persona.cedula.isNullOrBlank() &&
            persona.pasaporte.isNullOrBlank() &&
            pacienteDto.identificacionTemporal.isNullOrBlank()
        ) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("identificacion")
                    withMessage("Una identificación es obligatoria")
                },
            )
        }

        if (persona.fechaNacimiento == null) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("fecha_nacimiento")
                    withMessage("La fecha de nacimiento es obligatoria")
                },
            )
        }
    }

    /**
     * Validates the data required to create a new patient and linked user.
     * Includes checks like duplicate identity, missing fields, etc.
     *
     * @param pacienteDto DTO containing patient data to validate.
     * @return [ActualApiResponse] with validation results or errors.
     */
    private suspend fun validateCreatePacienteUsuario(pacienteDto: PacienteDto) = createResponseBuilder {
        val usuario = pacienteDto.persona.usuario

        if (usuario == null) {
            withError(
                ApiResponseCode.MISSING_INFORMATION,
                "Esta función necesita el usuario para el paciente",
            )
            return@createResponseBuilder
        }

        if (usuario.id == null && usuario.roles.any { it.nombre != null }) {
            withError(
                ApiResponseCode.NON_IDEMPOTENCE,
                "Utilice ID para el rol Paciente en esta función",
                "roles[]",
            )
        }

        if (usuario.roles.any { it.id?.let { id -> RolesEnum.getByPriority(id) != RolesEnum.PACIENTE } == true }) {
            withError(
                ApiResponseCode.VALIDATION_FAILED,
                "Esta función es solo para pacientes",
                "roles[]",
            )
        }

        if (pacienteDto.persona.sexo?.equals("X", ignoreCase = true) == true) {
            withWarning(
                ApiResponseCode.DEPRECATION_WARNING,
                "Sexo no definido afecta reglas de vacunación",
                "sexo",
            )
        }

        if (pacienteDto.persona.direccion.id == null) {
            withWarning(
                ApiResponseCode.NON_IDEMPOTENCE,
                "Debe trabajar con ID",
                "direccion",
            )
        }

        if (pacienteDto.createdAt.toLocalDate() != usuario.createdAt.toLocalDate()) {
            withError(
                ApiResponseCode.VALIDATION_FAILED,
                "created_at de Paciente y Usuario deben coincidir",
                "created_at",
            )
        }
    }
}
