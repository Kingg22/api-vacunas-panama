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

/** Service for {@link Persona}. */
@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaService implements IPersonaService {
    private final PersonaRepository personaRepository;

    public Optional<Persona> getPersona(@org.jetbrains.annotations.NotNull @NotNull String identifier) {
        FormatterUtil.FormatterResult result = FormatterUtil.formatToSearch(identifier);
        log.debug(
                "Searching Persona by cedula: {}, pasaporte: {}, correo: {}",
                result.cedula(),
                result.pasaporte(),
                result.correo());
        Optional<Persona> persona = this.personaRepository.findByCedulaOrPasaporteOrCorreo(
                result.cedula(), result.pasaporte(), result.correo());
        if (persona.isEmpty()) {
            log.debug("Searching Persona by username: {}", identifier);
            persona = this.personaRepository.findByUsuario_Username(identifier);
        }

        return persona;
    }

    public Optional<Persona> getPersonaByUserID(@org.jetbrains.annotations.NotNull UUID idUser) {
        return this.personaRepository.findByUsuario_Id(idUser);
    }
}
