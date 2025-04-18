package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import java.util.UUID

/** Service interface for managing and retrieving persona-related information. */
interface PersonaService {
    /**
     * Retrieves a [Persona] entity by an identifier
     * (maybe can be [PersonaDto.cedula], [PersonaDto.pasaporte], [PersonaDto.correo] or `PersonaDto.usuario.username`).
     *
     * @param identifier UUID of the persona to retrieve.
     * @return The persona if found, or null if not.
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto.username
     */
    @Deprecated(
        message = "This function be change to use DTO when jooq is set as ORM",
        replaceWith = ReplaceWith("getPersonaDto(identifier)"),
    )
    fun getPersona(identifier: String): Persona?

    /**
     * Retrieves a [PersonaDto] entity by an identifier
     * (maybe can be [PersonaDto.cedula], [PersonaDto.pasaporte], [PersonaDto.correo] or `PersonaDto.usuario.username`).
     *
     * @param identifier UUID of the persona to retrieve.
     * @return The persona if found, or empty if not.
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto.username
     */
    fun getPersonaDto(identifier: String): PersonaDto?

    /**
     * Retrieves a [PersonaDto] associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the persona.
     * @return The persona if found, or null if not.
     */
    fun getPersonaByUserID(idUser: UUID): PersonaDto?
}
