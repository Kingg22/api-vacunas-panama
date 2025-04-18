package io.github.kingg22.api.vacunas.panama.modules.vacuna.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.toDoctor
import io.github.kingg22.api.vacunas.panama.modules.doctor.service.DoctorService
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.toPaciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.toSede
import io.github.kingg22.api.vacunas.panama.modules.sede.service.SedeService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.toDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.extensions.toListDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.DosisRepository
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.VacunaRepository
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.returnIfErrors
import io.github.kingg22.api.vacunas.panama.util.ifPresentOrElse
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class VacunaServiceImpl(
    private val vacunaRepository: VacunaRepository,
    private val dosisRepository: DosisRepository,
    @Lazy private val doctorService: DoctorService,
    @Lazy private val sedeService: SedeService,
    @Lazy private val pacienteService: PacienteService,
) : VacunaService {
    private val log = logger()

    @Transactional
    override fun createDosis(insertDosisDto: InsertDosisDto): ApiContentResponse {
        val contentResponse = createResponseBuilder()
        val paciente = pacienteService.getPacienteById(insertDosisDto.pacienteId)?.toPaciente()
        val vacuna = vacunaRepository.findById(insertDosisDto.vacunaId).getOrNull()
        val sede = sedeService.getSedeById(insertDosisDto.sedeId)?.toSede()
        val doctor = insertDosisDto.doctorId?.let {
            doctorService.getDoctorById(it)
        }

        when {
            paciente == null -> {
                contentResponse.withError(
                    code = ApiResponseCode.NOT_FOUND,
                    message = "Paciente no encontrado",
                    property = "paciente_id",
                )
                return contentResponse.build()
            }

            vacuna == null -> {
                contentResponse.withError(
                    code = ApiResponseCode.NOT_FOUND,
                    message = "Vacuna no encontrada",
                    property = "vacuna_id",
                )
                return contentResponse.build()
            }

            sede == null -> {
                contentResponse.withError(
                    code = ApiResponseCode.NOT_FOUND,
                    message = "Sede no encontrada",
                    property = "sede_id",
                )
                return contentResponse.build()
            }

            doctor == null -> {
                contentResponse.withWarning(
                    code = ApiResponseCode.NOT_FOUND,
                    message = "Doctor no encontrado",
                    property = "doctor_id",
                )
            }
        }

        dosisRepository.findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente, vacuna).ifPresentOrElse({
            log.debug("Última dosis encontrada, ID: {}", it.id)
            if (!it.numeroDosis.isValidNew(insertDosisDto.numeroDosis)) {
                contentResponse.withError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "La dosis ${insertDosisDto.numeroDosis} no es válida. Último número de dosis ${it.numeroDosis}",
                    "numero_dosis",
                )
                return@ifPresentOrElse
            }
            log.debug("Nueva dosis cumple las reglas de secuencia en número de dosis")
        }) { log.debug("El paciente no tiene dosis previas") }
        contentResponse.build().returnIfErrors()?.let { return it }

        val dosis = dosisRepository.save(
            Dosis(
                paciente = paciente,
                numeroDosis = insertDosisDto.numeroDosis,
                vacuna = vacuna,
                sede = sede,
                lote = insertDosisDto.lote,
                doctor = doctor?.toDoctor(),
                createdAt = LocalDateTime.now(UTC),
            ),
        )
        contentResponse.withData("dosis", dosis.toDosisDto())
        return contentResponse.build()
    }

    @Cacheable(
        cacheNames = [CacheDuration.HUGE_VALUE],
        key = "'vacunas'",
        unless = "#result==null or #result.isEmpty()",
    )
    override fun getVacunasFabricante() = vacunaRepository.findAllIdAndNombreAndFabricante()

    @Transactional
    override fun getDosisByIdPacienteIdVacuna(idPaciente: UUID, idVacuna: UUID) =
        dosisRepository.findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(idPaciente, idVacuna).toListDosisDto()
}
