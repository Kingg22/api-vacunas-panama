package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO for view_pacientes_vacunas_enfermedades, details about [io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente]
 * add separate of view.
 */
@RegisterForReflection
@JvmRecord
data class ViewPacienteVacunaEnfermedadDto(
    @all:Size(max = 100) val vacuna: String,

    @all:JsonProperty("numero_dosis") val numeroDosis: String,

    @all:JsonProperty("edad_min_recomendada")
    val edadMinRecomendada: Short? = null,

    @all:JsonProperty("fecha_aplicacion")
    val fechaAplicacion: LocalDateTime,

    @all:JsonProperty("intervalo_recomendado_dosis")
    val intervaloRecomendadoDosis: Double? = null,

    @all:JsonProperty("intervalo_real_dosis")
    val intervaloRealDosis: Int? = null,

    @all:Size(max = 100) val sede: String? = null,

    @all:Size(max = 13) val dependencia: String? = null,

    @all:JsonProperty("id_paciente")
    @all:JsonIgnore
    val idPaciente: UUID? = null,

    @all:JsonProperty("id_vacuna") val idVacuna: UUID? = null,

    @all:JsonProperty("id_sede") val idSede: UUID? = null,

    @all:JsonProperty("id_dosis") val idDosis: UUID? = null,
) : Serializable
