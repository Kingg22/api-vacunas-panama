package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPacienteService {
    ApiContentResponse validateCreatePacienteUsuario(PacienteDto pacienteDto);

    ApiContentResponse createPaciente(PacienteDto pacienteDto);

    PacienteDto getPacienteDtoById(UUID id);

    Optional<Paciente> getPacienteByUserID(UUID idUser);

    Optional<Paciente> getPacienteById(UUID idPaciente);

    List<ViewPacienteVacunaEnfermedadDto> getViewVacunaEnfermedad(UUID id);
}
