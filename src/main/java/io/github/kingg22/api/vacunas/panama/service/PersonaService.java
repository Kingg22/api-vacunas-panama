package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona;
import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository;
import io.github.kingg22.api.vacunas.panama.modules.persona.service.IPersonaService;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service for {@link Persona}. */
@Service
public class PersonaService implements IPersonaService {
    private final PersonaRepository personaRepository;
    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @org.jetbrains.annotations.NotNull
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

    @org.jetbrains.annotations.NotNull
    public Optional<Persona> getPersonaByUserID(@org.jetbrains.annotations.NotNull UUID idUser) {
        return this.personaRepository.findByUsuario_Id(idUser);
    }
}
