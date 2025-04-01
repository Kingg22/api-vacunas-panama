package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for insert new [io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis]  */
@JvmRecord
data class InsertDosisDto @JvmOverloads constructor(
    @field:JsonProperty(value = "paciente_id") @param:JsonProperty(value = "paciente_id") val pacienteId: UUID,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @param:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:JsonProperty(value = "fecha_aplicacion")
    @param:JsonProperty(value = "fecha_aplicacion")
    @field:PastOrPresent
    @param:PastOrPresent
    val fechaAplicacion: LocalDateTime? = null,

    @field:JsonProperty(value = "numero_dosis")
    @param:JsonProperty(value = "numero_dosis")
    val numeroDosis: NumDosisEnum,

    @field:JsonProperty(value = "vacuna_id") @param:JsonProperty(value = "vacuna_id") val vacunaId: UUID,

    @field:JsonProperty(value = "sede_id") @param:JsonProperty(value = "sede_id") val sedeId: UUID,

    @field:Size(max = 50) @param:Size(max = 50) val lote: String? = null,

    @field:JsonProperty(value = "doctor_id") @param:JsonProperty(value = "doctor_id") val doctorId: UUID,
) : Serializable
