package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Fabricante;
import io.github.kingg22.api_vacunas_panama.persistence.repository.FabricanteRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for {@link Fabricante}.
 */
@Service
@RequiredArgsConstructor
public class FabricanteService {
    private final FabricanteRepository fabricanteRepository;

    public Optional<Fabricante> getFabricante(@NotNull String licencia) {
        return fabricanteRepository.findByLicencia(licencia);
    }

    public Optional<Fabricante> getFabricanteByUserID(@NotNull UUID idUser) {
        return this.fabricanteRepository.findByUsuario_Id(idUser);
    }

}
