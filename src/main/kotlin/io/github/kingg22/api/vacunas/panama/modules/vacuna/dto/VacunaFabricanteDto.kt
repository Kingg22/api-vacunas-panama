package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

@RegisterForReflection
@JvmRecord
data class VacunaFabricanteDto(
    @all:JsonProperty(value = "id_vacuna")
    val idVacuna: UUID,

    @all:JsonProperty(value = "nombre_vacuna")
    @all:Size(max = 100)
    @all:NotBlank
    val nombreVacuna: String,

    @all:JsonProperty("id_fabricante")
    val idFabricante: UUID? = null,

    @all:JsonProperty(value = "nombre_fabricante")
    @all:Size(max = 100)
    val nombreFabricante: String? = null,
) : Serializable
