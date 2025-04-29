package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import java.util.UUID

/** Service interface for managing and retrieving persona-related information. */
interface PersonaService {
    /**
     * Retrieves a [PersonaModel] by an identifier
     * (maybe can be [PersonaDto.cedula], [PersonaDto.pasaporte], [PersonaDto.correo] or `PersonaDto.usuario.username`).
     *
     * @param identifier String identifier of the persona to retrieve.
     * @return The persona if found, or null if not.
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto.username
     */
    suspend fun getPersona(identifier: String): PersonaModel?

    /**
     * Retrieves a [PersonaDto] associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the persona.
     * @return The persona if found, or null if not.
     */
    suspend fun getPersonaByUserID(idUser: UUID): PersonaDto?
}
