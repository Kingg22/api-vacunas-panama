package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.toPersonaModel
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.persistence.PersonaPersistenceService
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class PersonaServiceImpl(private val personaPersistenceService: PersonaPersistenceService) : PersonaService {
    override suspend fun getPersona(identifier: String): PersonaModel? {
        val result = formatToSearch(identifier)
        val personaOpt = personaPersistenceService.findByCedulaOrPasaporteOrCorreo(
            result.cedula,
            result.pasaporte,
            result.correo,
        )
        return (personaOpt ?: personaPersistenceService.findByUsuarioUsername(identifier))
            ?.toPersonaDto()
            ?.toPersonaModel()
    }

    override suspend fun getPersonaByUserID(idUser: UUID) =
        personaPersistenceService.findByUsuarioId(idUser)?.toPersonaDto()
}
