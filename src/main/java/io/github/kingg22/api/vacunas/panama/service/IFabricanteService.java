package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface IFabricanteService {
    Optional<Fabricante> getFabricante(@org.jetbrains.annotations.NotNull @NotNull String licencia);

    Optional<Fabricante> getFabricanteByUserID(@org.jetbrains.annotations.NotNull @NotNull UUID idUser);
}
