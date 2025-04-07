package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class PersonaServiceImpl(private val personaRepository: PersonaRepository) : IPersonaService {
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

    override fun getPersonaByUserID(idUser: @NotNull UUID) = personaRepository.findByUsuario_Id(idUser)
}
