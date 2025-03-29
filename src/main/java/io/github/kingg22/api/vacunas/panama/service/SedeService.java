package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto;
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede;
import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository;
import io.github.kingg22.api.vacunas.panama.modules.sede.service.ISedeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Service for {@link Sede}. */
@Service
@RequiredArgsConstructor
public class SedeService implements ISedeService {
    private final SedeRepository sedeRepository;

    @NotNull
    @Cacheable(cacheNames = "massive", key = "'sedesNombre'")
    public List<UUIDNombreDto> getIdNombreSedes() {
        return sedeRepository.findAllIdAndNombre();
    }

    @NotNull
    public Optional<Sede> getSedeById(@NotNull UUID id) {
        return sedeRepository.findById(id);
    }
}
