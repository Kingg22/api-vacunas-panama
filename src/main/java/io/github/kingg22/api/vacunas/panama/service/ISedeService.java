package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto;
import java.util.List;

public interface ISedeService {
    List<UUIDNombreDto> getIdNombreSedes();
}
