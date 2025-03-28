package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Sede;
import io.github.kingg22.api.vacunas.panama.persistence.repository.SedeRepository;
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Service for {@link Sede}. */
@Service
@RequiredArgsConstructor
public class SedeService implements ISedeService {
    private final SedeRepository sedeRepository;

    @Cacheable(cacheNames = "massive", key = "'sedesNombre'")
    public List<UUIDNombreDto> getIdNombreSedes() {
        return sedeRepository.findAllIdAndNombre();
    }

    public Optional<Sede> getSedeById(UUID id) {
        return sedeRepository.findById(id);
    }
}
