package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto;
import io.github.kingg22.api.vacunas.panama.web.dto.InsertDosisDto;
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto;
import java.util.List;
import java.util.UUID;

public interface IVacunaService {
    ApiContentResponse createDosis(InsertDosisDto insertDosisDto);

    List<VacunaFabricanteDto> getVacunasFabricante();

    List<DosisDto> getDosisByIdPacienteIdVacuna(UUID idPaciente, UUID idVacuna);
}
