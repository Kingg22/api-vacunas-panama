package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import io.github.kingg22.api.vacunas.panama.persistence.repository.PersonaRepository;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for {@link Persona}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaService {
    private final PersonaRepository personaRepository;

    Optional<Persona> getPersona(@NotNull String identifier) {
        String[] result = FormatterUtil.formatToSearch(identifier);
        log.debug("Searching Persona by cedula: {}, pasaporte: {}, correo: {}", result[0], result[1], result[2]);
        Optional<Persona> persona =
                this.personaRepository.findByCedulaOrPasaporteOrCorreo(result[0], result[1], result[2]);
        if (persona.isEmpty()) {
            log.debug("Searching Persona by username: {}", identifier);
            persona = this.personaRepository.findByUsuario_Username(identifier);
        }

        return persona;
    }

    Optional<Persona> getPersonaByUserID(UUID idUser) {
        return this.personaRepository.findByUsuario_Id(idUser);
    }
}
