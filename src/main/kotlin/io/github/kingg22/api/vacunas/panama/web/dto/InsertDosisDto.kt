package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for insert new [io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis]  */
@JvmRecord
data class InsertDosisDto @JvmOverloads constructor(
    @JsonProperty(value = "paciente_id") val pacienteId: UUID,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty(value = "fecha_aplicacion")
    @PastOrPresent
    val fechaAplicacion: LocalDateTime? = null,
    @JsonProperty(value = "numero_dosis") val numeroDosis: NumDosisEnum,
    @JsonProperty(value = "vacuna_id") val vacunaId: UUID,
    @JsonProperty(value = "sede_id") val sedeId: UUID,
    @Size(max = 50) val lote: String? = null,
    @JsonProperty(value = "doctor_id") val doctorId: UUID,
) : Serializable
