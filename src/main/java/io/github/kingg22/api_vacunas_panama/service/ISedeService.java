package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.web.dto.UUIDNombreDto;

import java.util.List;

public interface ISedeService {
    List<UUIDNombreDto> getIdNombreSedes();
}
