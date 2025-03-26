package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion;
import io.github.kingg22.api.vacunas.panama.web.dto.DireccionDto;
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface IDireccionService {
    List<DistritoDto> getDistritosDto();

    List<ProvinciaDto> getProvinciasDto();

    Direccion createDireccion(@NotNull @Valid DireccionDto direccionDto);
}
