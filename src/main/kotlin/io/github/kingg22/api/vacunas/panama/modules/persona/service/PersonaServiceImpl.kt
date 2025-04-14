package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class PersonaServiceImpl(private val personaRepository: PersonaRepository) : PersonaService {
    override fun getPersona(identifier: @NotNull String): Optional<PersonaDto> {
        val result = formatToSearch(identifier)
        val personaOpt = this.personaRepository.findByCedulaOrPasaporteOrCorreo(
            result.cedula,
            result.pasaporte,
            result.correo,
        )
        return if (personaOpt.isPresent) {
            personaOpt.map { it.toPersonaDto() }
        } else {
            personaRepository.findByUsuario_Username(identifier).map { it.toPersonaDto() }
        }
    }

    override fun getPersonaByUserID(idUser: @NotNull UUID): Optional<PersonaDto> =
        personaRepository.findByUsuario_Id(idUser).map { it.toPersonaDto() }
}
