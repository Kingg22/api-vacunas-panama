package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.web.dto.UUIDNombreDto;

import java.util.List;

public interface ISedeService {
    List<UUIDNombreDto> getIdNombreSedes();
}
