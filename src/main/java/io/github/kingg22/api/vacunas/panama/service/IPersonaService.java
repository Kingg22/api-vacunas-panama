package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface IPersonaService {
    Optional<Persona> getPersona(@org.jetbrains.annotations.NotNull @NotNull String identifier);

    Optional<Persona> getPersonaByUserID(@org.jetbrains.annotations.NotNull @NotNull UUID idUser);
}
