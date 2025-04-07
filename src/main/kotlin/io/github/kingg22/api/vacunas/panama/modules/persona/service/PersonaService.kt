package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving persona-related information. */
interface PersonaService {
    /**
     * Retrieves a [Persona] entity by an identifier (maybe can be [Persona.cedula], [Persona.pasaporte], [Persona.correo] or `Persona.usuario.username`.
     *
     * @param identifier UUID of the persona to retrieve.
     * @return [Optional] containing the persona if found, or empty if not.
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario.username
     */
    fun getPersona(identifier: String): Optional<Persona>

    /**
     * Retrieves a [Persona] entity associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the persona.
     * @return [Optional] containing the persona if found, or empty if not.
     */
    fun getPersonaByUserID(idUser: UUID): Optional<Persona>
}
