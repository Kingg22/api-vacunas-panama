package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for insert new [io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis]  */
@RegisterForReflection
@JvmRecord
data class InsertDosisDto(
    @all:JsonProperty(value = "paciente_id")
    val pacienteId: UUID,

    @all:JsonProperty(value = "fecha_aplicacion")
    @all:PastOrPresent
    val fechaAplicacion: LocalDateTime? = null,

    @all:JsonProperty(value = "numero_dosis")
    val numeroDosis: NumDosisEnum,

    @all:JsonProperty(value = "vacuna_id")
    val vacunaId: UUID,

    @all:JsonProperty(value = "sede_id")
    val sedeId: UUID,

    @all:Size(max = 50)
    val lote: String? = null,

    @all:JsonProperty(value = "doctor_id")
    val doctorId: UUID? = null,
) : Serializable
