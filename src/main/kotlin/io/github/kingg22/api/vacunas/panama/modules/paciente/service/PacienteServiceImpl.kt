package io.github.kingg22.api.vacunas.panama.modules.paciente.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.service.DireccionService
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.repository.PacienteRepository
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.returnIfErrors
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil
import io.github.kingg22.api.vacunas.panama.util.logger
import org.hibernate.Hibernate
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Service
class PacienteServiceImpl(
    private val pacienteRepository: PacienteRepository,
    @Lazy private val direccionService: DireccionService,
) : PacienteService {
    private val log = logger()

    override fun validateCreatePacienteUsuario(pacienteDto: PacienteDto): ApiContentResponse = createResponseBuilder {
        val usuario = pacienteDto.persona.usuario

        if (usuario == null) {
            withError(
                ApiResponseCode.MISSING_INFORMATION,
                "Esta función necesita el usuario para el paciente",
            )
            return@createResponseBuilder
        }

        if (usuario.id == null && usuario.roles?.any { it.nombre != null } == true) {
            withError(
                ApiResponseCode.NON_IDEMPOTENCE,
                "Utilice ID para el rol Paciente en esta función",
                "roles[]",
            )
        }

        if (usuario.roles?.any { it.id?.let { id -> RolesEnum.getByPriority(id) != RolesEnum.PACIENTE } == true } ==
            true
        ) {
            withError(
                ApiResponseCode.VALIDATION_FAILED,
                "Esta función es solo para pacientes",
                "roles[]",
            )
        }

        if (pacienteDto.persona.sexo?.toString()?.equals("X", ignoreCase = true) == true) {
            withWarning(
                ApiResponseCode.DEPRECATION_WARNING,
                "Sexo no definido afecta reglas de vacunación",
                "sexo",
            )
        }

        if (pacienteDto.persona.direccion?.id == null) {
            withWarning(
                ApiResponseCode.NON_IDEMPOTENCE,
                "Debe trabajar con ID",
                "direccion",
            )
        }

        val createdPaciente = pacienteDto.createdAt
        val createdUsuario = usuario.createdAt
        if (createdPaciente != null && createdUsuario != null && createdPaciente != createdUsuario) {
            withError(
                ApiResponseCode.VALIDATION_FAILED,
                "created_at de Paciente y Usuario deben coincidir",
                "created_at",
            )
        }
    }

    @Transactional
    override fun createPaciente(pacienteDto: PacienteDto): ApiContentResponse {
        val response = createContentResponse()
        response.addErrors(validatePacienteExist(pacienteDto))
        response.addErrors(validateCreatePaciente(pacienteDto))
        response.mergeContentResponse(validateCreatePacienteUsuario(pacienteDto))

        response.returnIfErrors()?.let { return it }

        val direccion = pacienteDto.persona.direccion?.let {
            direccionService.getDireccionByDto(it).orElseGet { direccionService.createDireccion(it) }
        } ?: direccionService.getDireccionDefault()

        val paciente = pacienteRepository.save(
            Paciente.builder {
                identificacionTemporal(pacienteDto.identificacionTemporal)
                createdAt(pacienteDto.createdAt ?: LocalDateTime.now(UTC))
                nombre(pacienteDto.persona.nombre)
                nombre2(pacienteDto.persona.nombre2)
                apellido1(pacienteDto.persona.apellido1)
                apellido2(pacienteDto.persona.apellido2)
                fechaNacimiento(pacienteDto.persona.fechaNacimiento)
                cedula(pacienteDto.persona.cedula)
                pasaporte(pacienteDto.persona.pasaporte)
                telefono(pacienteDto.persona.telefono)
                correo(pacienteDto.persona.correo)
                sexo(pacienteDto.persona.sexo)
                direccion(direccion)
                estado(pacienteDto.persona.estado ?: "ACTIVO")
                disabled(pacienteDto.persona.disabled)
            },
        )

        Hibernate.initialize(paciente.usuario)
        Hibernate.initialize(paciente.direccion)
        paciente.direccion.distrito?.let {
            Hibernate.initialize(it)
            Hibernate.initialize(it.provincia)
        }

        response.addData("paciente", paciente.toPacienteDto())
        return response
    }

    override fun getPacienteDtoById(id: UUID) = pacienteRepository.findById(id).orElseThrow().toPacienteDto()

    override fun getPacienteByUserID(idUser: UUID) = pacienteRepository.findByUsuario_Id(idUser)

    override fun getPacienteById(idPaciente: UUID) = pacienteRepository.findById(idPaciente)

    @Cacheable(cacheNames = ["cache"], key = "'view_vacuna_enfermedad'.concat(#id)")
    override fun getViewVacunaEnfermedad(id: UUID) = pacienteRepository.findAllFromViewVacunaEnfermedad(id)

    fun validatePacienteExist(pacienteDto: PacienteDto) = buildList {
        val persona = pacienteDto.persona

        persona.cedula?.let {
            val formatted = FormatterUtil.formatCedula(it)
            pacienteDto.changePersona(persona.changeCedulaOrID(formatted))
            if (pacienteRepository.findByCedula(formatted).isPresent) {
                add(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.VALIDATION_FAILED)
                        withProperty("cedula")
                        withMessage("Ya existe un paciente con esta cédula")
                    },
                )
            }
        }

        if (!persona.pasaporte.isNullOrBlank() && pacienteRepository.findByPasaporte(persona.pasaporte).isPresent) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("pasaporte")
                    withMessage("Ya existe un paciente con este pasaporte")
                },
            )
        }

        if (!pacienteDto.identificacionTemporal.isNullOrBlank() &&
            pacienteRepository.findByIdentificacionTemporal(pacienteDto.identificacionTemporal).isPresent
        ) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("identificacionTemporal")
                    withMessage("Ya existe un paciente con esta identificación temporal")
                },
            )
        }

        if (!persona.correo.isNullOrBlank() && pacienteRepository.findByCorreo(persona.correo).isPresent) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("correo")
                    withMessage("Ya existe un paciente con este correo")
                },
            )
        }

        if (!persona.telefono.isNullOrBlank() && pacienteRepository.findByTelefono(persona.telefono).isPresent) {
            add(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.VALIDATION_FAILED)
                    withProperty("telefono")
                    withMessage("Ya existe un paciente con este teléfono")
                },
            )
        }
    }

    fun validateCreatePaciente(pacienteDto: PacienteDto) = buildList {
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
                    withProperty("fechaNacimiento")
                    withMessage("La fecha de nacimiento es obligatoria")
                },
            )
        }
    }
}
