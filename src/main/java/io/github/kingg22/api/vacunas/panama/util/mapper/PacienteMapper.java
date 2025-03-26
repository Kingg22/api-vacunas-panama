package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = PersonaMapper.class)
public interface PacienteMapper {
    PacienteDto toDto(Paciente paciente);
}
