package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving persona-related information. */
interface PersonaService {
    /**
     * Retrieves a [PersonaDto] entity by an identifier
     * (maybe can be [PersonaDto.cedula], [PersonaDto.pasaporte], [PersonaDto.correo] or `PersonaDto.usuario.username`).
     *
     * @param identifier UUID of the persona to retrieve.
     * @return [Optional] containing the persona if found, or empty if not.
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto.username
     */
    fun getPersona(identifier: String): Optional<PersonaDto>

    /**
     * Retrieves a [PersonaDto] associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the persona.
     * @return [Optional] containing the persona if found, or empty if not.
     */
    fun getPersonaByUserID(idUser: UUID): Optional<PersonaDto>
}
