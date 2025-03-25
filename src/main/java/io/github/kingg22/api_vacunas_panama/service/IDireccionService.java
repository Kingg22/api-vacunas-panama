package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Direccion;
import io.github.kingg22.api_vacunas_panama.web.dto.DireccionDto;
import io.github.kingg22.api_vacunas_panama.web.dto.DistritoDto;
import io.github.kingg22.api_vacunas_panama.web.dto.ProvinciaDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IDireccionService {
    List<DistritoDto> getDistritosDto();

    List<ProvinciaDto> getProvinciasDto();

    Direccion createDireccion(@NotNull @Valid DireccionDto direccionDto);
}
