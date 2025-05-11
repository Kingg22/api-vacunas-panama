package io.github.kingg22.api.vacunas.panama.modules.persona.domain

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DireccionModel
import io.github.kingg22.api.vacunas.panama.modules.usuario.domain.UsuarioModel
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a person in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class PersonaModel(
    val id: UUID? = null,
    val nombre: String? = null,
    val nombre2: String? = null,
    val apellido1: String? = null,
    val apellido2: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val fechaNacimiento: LocalDateTime? = null,
    val cedula: String? = null,
    val pasaporte: String? = null,
    val direccion: DireccionModel? = null,
    val usuario: UsuarioModel? = null,
)
