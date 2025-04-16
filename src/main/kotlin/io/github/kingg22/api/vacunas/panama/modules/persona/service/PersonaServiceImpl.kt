package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class PersonaServiceImpl(private val personaRepository: PersonaRepository) : PersonaService {
    override fun getPersona(identifier: @NotNull String): Optional<Persona> {
        val result = formatToSearch(identifier)
        val personaOpt = this.personaRepository.findByCedulaOrPasaporteOrCorreo(
            result.cedula,
            result.pasaporte,
            result.correo,
        )
        return if (personaOpt.isPresent) {
            personaOpt
        } else {
            personaRepository.findByUsuario_Username(identifier)
        }
    }

    override fun getPersonaDto(identifier: String): PersonaDto? = getPersona(identifier).getOrNull()?.toPersonaDto()

    override fun getPersonaByUserID(idUser: @NotNull UUID): Optional<PersonaDto> =
        personaRepository.findByUsuario_Id(idUser).map { it.toPersonaDto() }
}
