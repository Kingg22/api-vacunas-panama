package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO for view_pacientes_vacunas_enfermedades, details about [io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente]
 * add separate of view.
 */
@JvmRecord
data class ViewPacienteVacunaEnfermedadDto @JvmOverloads constructor(
    @JsonProperty("id_paciente") @JsonIgnore val idPaciente: UUID? = null,
    @JsonProperty("id_dosis") val idDosis: UUID? = null,
    @JsonProperty("id_vacuna") val idVacuna: UUID? = null,
    @Size(max = 100) val vacuna: String,
    @JsonProperty("numero_dosis") val numeroDosis: NumDosisEnum,
    val enfermedades: List<IdNombreDto> = emptyList(),
    @JsonProperty("edad_min_recomendada") val edadMinRecomendada: Short? = null,
    @JsonProperty("fecha_aplicacion") val fechaAplicacion: LocalDateTime,
    @JsonProperty("intervalo_recomendado_dosis") val intervaloRecomendadoDosis: Double? = null,
    @JsonProperty("intervalo_real_dosis") val intervaloRealDosis: Int? = null,
    @JsonProperty("id_sede") val idSede: UUID? = null,
    @Size(max = 100) val sede: String? = null,
    @Size(max = 13) val dependencia: String? = null,
) : Serializable {

    @Suppress("unused")
    @JvmOverloads
    constructor(
        vacuna: String,
        numeroDosis: String,
        enfermedadesStr: String,
        edadMinima: Short? = null,
        fechaAplicacion: LocalDateTime,
        intervaloRecomendado: Double? = null,
        intervaloReal: Int? = null,
        sede: String? = null,
        dependencia: String? = null,
        idPaciente: UUID? = null,
        idVacuna: UUID? = null,
        idSede: UUID? = null,
        idDosis: UUID? = null,
        idsEnfermedadesStr: String,
    ) : this(
        idPaciente = idPaciente,
        idDosis = idDosis,
        idVacuna = idVacuna,
        vacuna = vacuna,
        numeroDosis = NumDosisEnum.fromValue(numeroDosis.trim().uppercase()),
        enfermedades = parseEnfermedades(idsEnfermedadesStr, enfermedadesStr),
        edadMinRecomendada = edadMinima,
        fechaAplicacion = fechaAplicacion,
        intervaloRecomendadoDosis = intervaloRecomendado,
        intervaloRealDosis = intervaloReal,
        idSede = idSede,
        sede = sede,
        dependencia = dependencia,
    )

    companion object {
        private fun parseEnfermedades(idsStr: String, nombresStr: String): List<IdNombreDto> {
            val ids = idsStr.split(",").mapNotNull { it.trim().toIntOrNull() }
            val nombres = nombresStr.split(",").map { it.trim() }
            require(ids.size == nombres.size) { "The ID and name lists must be the same size" }
            return ids.zip(nombres) { id, nombre -> IdNombreDto(id, nombre) }
        }
    }
}
