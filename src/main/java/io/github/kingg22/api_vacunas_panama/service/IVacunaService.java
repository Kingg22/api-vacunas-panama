package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.response.IApiContentResponse;
import io.github.kingg22.api_vacunas_panama.web.dto.DosisDto;
import io.github.kingg22.api_vacunas_panama.web.dto.InsertDosisDto;
import io.github.kingg22.api_vacunas_panama.web.dto.VacunaFabricanteDto;

import java.util.List;
import java.util.UUID;

public interface IVacunaService {
    IApiContentResponse createDosis(InsertDosisDto insertDosisDto);

    List<VacunaFabricanteDto> getVacunasFabricante();

    List<DosisDto> getDosisByIdPacienteIdVacuna(UUID idPaciente, UUID idVacuna);
}
