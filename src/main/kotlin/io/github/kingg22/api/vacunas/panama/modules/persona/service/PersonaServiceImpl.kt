package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PersonaServiceImpl(private val personaRepository: PersonaRepository) : PersonaService {
    @Deprecated(
        "This function be change to use DTO when jooq is set as ORM",
        replaceWith = ReplaceWith("getPersonaDto(identifier)"),
    )
    override fun getPersona(identifier: @NotNull String): Persona? {
        val result = formatToSearch(identifier)
        val personaOpt = this.personaRepository.findByCedulaOrPasaporteOrCorreo(
            result.cedula,
            result.pasaporte,
            result.correo,
        )
        return personaOpt ?: personaRepository.findByUsuario_Username(identifier)
    }

    override fun getPersonaDto(identifier: String): PersonaDto? = getPersona(identifier)?.toPersonaDto()

    override fun getPersonaByUserID(idUser: @NotNull UUID): PersonaDto? =
        personaRepository.findByUsuario_Id(idUser)?.toPersonaDto()
}
