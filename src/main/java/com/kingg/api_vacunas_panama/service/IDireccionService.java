package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.persistence.entity.Direccion;
import com.kingg.api_vacunas_panama.web.dto.DireccionDto;
import com.kingg.api_vacunas_panama.web.dto.DistritoDto;
import com.kingg.api_vacunas_panama.web.dto.ProvinciaDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IDireccionService {
    List<DistritoDto> getDistritosDto();

    List<ProvinciaDto> getProvinciasDto();

    Direccion createDireccion(@NotNull @Valid DireccionDto direccionDto);
}
