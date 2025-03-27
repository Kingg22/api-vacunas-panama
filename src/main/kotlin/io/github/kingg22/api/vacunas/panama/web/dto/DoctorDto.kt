package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor]  */
class DoctorDto @JvmOverloads constructor(
    @Size(max = 20)
    val idoneidad: String? = null,

    @Size(max = 100)
    val categoria: String? = null,

    @JsonProperty(value = "created_at")
    @PastOrPresent
    val createdAt: LocalDateTime? = null,

    @JsonProperty(value = "updated_at")
    @PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : PersonaDto(),
    Serializable
