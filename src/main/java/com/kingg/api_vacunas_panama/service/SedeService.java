package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.persistence.entity.Sede;
import com.kingg.api_vacunas_panama.persistence.repository.SedeRepository;
import com.kingg.api_vacunas_panama.web.dto.UUIDNombreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for {@link Sede}.
 */
@Service
@RequiredArgsConstructor
public class SedeService implements ISedeService {
    private final SedeRepository sedeRepository;

    @Cacheable(cacheNames = "massive", key = "'sedesNombre'")
    public List<UUIDNombreDto> getIdNombreSedes() {
        return sedeRepository.findAllIdAndNombre();
    }

    Optional<Sede> getSedeById(UUID id) {
        return sedeRepository.findById(id);
    }

}
