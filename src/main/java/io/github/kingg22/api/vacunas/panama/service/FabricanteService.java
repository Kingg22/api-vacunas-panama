package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.repository.FabricanteRepository;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.IFabricanteService;
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

    @org.jetbrains.annotations.NotNull
    public Optional<Fabricante> getFabricante(@org.jetbrains.annotations.NotNull @NotNull String licencia) {
        return fabricanteRepository.findByLicencia(licencia);
    }

    @org.jetbrains.annotations.NotNull
    public Optional<Fabricante> getFabricanteByUserID(@org.jetbrains.annotations.NotNull @NotNull UUID idUser) {
        return this.fabricanteRepository.findByUsuario_Id(idUser);
    }
}
