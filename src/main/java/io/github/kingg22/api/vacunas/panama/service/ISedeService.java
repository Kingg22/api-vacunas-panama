package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Sede;
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISedeService {
    List<UUIDNombreDto> getIdNombreSedes();

    Optional<Sede> getSedeById(UUID id);
}
