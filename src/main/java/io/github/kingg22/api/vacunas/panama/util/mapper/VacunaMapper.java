package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna;
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {FabricanteMapper.class})
public interface VacunaMapper {
    VacunaDto toDto(Vacuna vacuna);
}
