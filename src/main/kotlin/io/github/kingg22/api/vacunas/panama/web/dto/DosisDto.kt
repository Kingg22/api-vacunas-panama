package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis]  */
@JvmRecord
data class DosisDto @JvmOverloads constructor(
    val id: UUID,
    @Valid val paciente: PacienteDto,
    @JsonProperty(value = "fecha_aplicacion") val fechaAplicacion: LocalDateTime,
    @JsonProperty(value = "numero_dosis") val numeroDosis: NumDosisEnum,
    @Valid val vacuna: VacunaDto,
    @Valid val sede: SedeDto,
    @Valid val doctor: DoctorDto? = null,
    @Size(max = 50) val lote: String? = null,
    @JsonProperty(value = "created_at") val createdAt: @PastOrPresent LocalDateTime,
    @JsonProperty(value = "updated_at") val updatedAt: @PastOrPresent LocalDateTime? = null,
) : Serializable
