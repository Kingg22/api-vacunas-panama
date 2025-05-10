package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis] */
@RegisterForReflection
@KonvertFrom(Dosis::class, [Mapping("numeroDosis", expression = "NumDosisEnum.fromValue(it.numeroDosis)")])
@JvmRecord
data class DosisDto(
    val id: UUID? = null,

    @all:Valid
    val paciente: PacienteDto,

    @all:JsonProperty(value = "fecha_aplicacion")
    @all:PastOrPresent
    val fechaAplicacion: LocalDateTime,

    @all:JsonProperty(value = "numero_dosis")
    val numeroDosis: NumDosisEnum,

    @all:Valid
    val vacuna: VacunaDto,

    @all:Valid
    val sede: SedeDto,

    @all:Valid
    val doctor: DoctorDto? = null,

    @all:Size(max = 50)
    val lote: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime,

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object
}
