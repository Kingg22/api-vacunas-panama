package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis]  */
@JvmRecord
data class DosisDto(
    val id: UUID? = null,

    @field:Valid @param:Valid val paciente: PacienteDto,

    @field:JsonProperty(value = "fecha_aplicacion")
    @param:JsonProperty(value = "fecha_aplicacion")
    val fechaAplicacion: LocalDateTime,

    @field:JsonProperty(value = "numero_dosis")
    @param:JsonProperty(value = "numero_dosis")
    val numeroDosis: NumDosisEnum,

    @field:Valid @param:Valid val vacuna: VacunaDto,

    @field:Valid @param:Valid val sede: SedeDto,

    @field:Valid @param:Valid val doctor: DoctorDto? = null,

    @field:Size(max = 50) @param:Size(max = 50) val lote: String? = null,

    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime,

    @field:JsonProperty(value = "updated_at")
    @param:JsonProperty(value = "updated_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable
