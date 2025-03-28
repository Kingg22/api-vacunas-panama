package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.persistence.repository.FabricanteRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service for {@link Fabricante}. */
@Service
@RequiredArgsConstructor
public class FabricanteService implements IFabricanteService {
    private final FabricanteRepository fabricanteRepository;

    public Optional<Fabricante> getFabricante(@org.jetbrains.annotations.NotNull @NotNull String licencia) {
        return fabricanteRepository.findByLicencia(licencia);
    }

    public Optional<Fabricante> getFabricanteByUserID(@org.jetbrains.annotations.NotNull @NotNull UUID idUser) {
        return this.fabricanteRepository.findByUsuario_Id(idUser);
    }
}
