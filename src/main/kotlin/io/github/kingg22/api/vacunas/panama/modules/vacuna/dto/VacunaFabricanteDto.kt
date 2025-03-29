package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

@JvmRecord
data class VacunaFabricanteDto(
    @field:JsonProperty(value = "id_vacuna") @param:JsonProperty(value = "id_vacuna") val idVacuna: UUID,

    @field:Size(max = 100)
    @param:Size(max = 100)
    @field:JsonProperty(value = "nombre_vacuna")
    @param:JsonProperty(value = "nombre_vacuna")
    @field:NotBlank
    @param:NotBlank
    val nombreVacuna: String,

    @field:JsonProperty(value = "id_fabricante")
    @param:JsonProperty(value = "id_fabricante")
    val idFabricante: UUID,

    @field:Size(max = 100)
    @param:Size(max = 100)
    @field:JsonProperty(value = "nombre_fabricante")
    @param:JsonProperty(value = "nombre_fabricante")
    val nombreFabricante: String,
) : Serializable
