package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.response.IApiContentResponse;
import com.kingg.api_vacunas_panama.web.dto.PacienteDto;
import com.kingg.api_vacunas_panama.web.dto.ViewPacienteVacunaEnfermedadDto;

import java.util.List;
import java.util.UUID;

public interface IPacienteService {
    IApiContentResponse validateCreatePacienteUsuario(PacienteDto pacienteDto);

    IApiContentResponse createPaciente(PacienteDto pacienteDto);

    PacienteDto getPacienteDtoById(UUID id);

    List<ViewPacienteVacunaEnfermedadDto> getViewVacunaEnfermedad(UUID id);
}
