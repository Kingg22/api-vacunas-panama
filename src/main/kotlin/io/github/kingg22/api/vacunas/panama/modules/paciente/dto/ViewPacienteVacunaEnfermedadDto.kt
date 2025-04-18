package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO for view_pacientes_vacunas_enfermedades, details about [io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente]
 * add separate of view.
 */
@JvmRecord
data class ViewPacienteVacunaEnfermedadDto(
    @param:Size(max = 100) @field:Size(max = 100) val vacuna: String,

    @param:JsonProperty("numero_dosis") @field:JsonProperty("numero_dosis") val numeroDosis: String,

    @param:JsonProperty("edad_min_recomendada") @field:JsonProperty("edad_min_recomendada")
    val edadMinRecomendada: Short? = null,

    @param:JsonProperty("fecha_aplicacion") @field:JsonProperty("fecha_aplicacion")
    val fechaAplicacion: LocalDateTime,

    @param:JsonProperty("intervalo_recomendado_dosis") @field:JsonProperty("intervalo_recomendado_dosis")
    val intervaloRecomendadoDosis: Double? = null,

    @param:JsonProperty("intervalo_real_dosis") @field:JsonProperty("intervalo_real_dosis")
    val intervaloRealDosis: Int? = null,

    @param:Size(max = 100) @field:Size(max = 100) val sede: String? = null,

    @param:Size(max = 13) @field:Size(max = 13) val dependencia: String? = null,

    @param:JsonProperty("id_paciente") @field:JsonProperty("id_paciente") @field:JsonIgnore
    val idPaciente: UUID? = null,

    @param:JsonProperty("id_vacuna") @field:JsonProperty("id_vacuna") val idVacuna: UUID? = null,

    @param:JsonProperty("id_sede") @field:JsonProperty("id_sede") val idSede: UUID? = null,

    @param:JsonProperty("id_dosis") @field:JsonProperty("id_dosis") val idDosis: UUID? = null,
) : Serializable
