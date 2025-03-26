package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna;
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface VacunaMapper {

    VacunaDto toDto(Vacuna vacuna);

    @Mapping(target = "sedesInventarios", ignore = true)
    @Mapping(target = "dosis", ignore = true)
    Vacuna toEntity(VacunaDto vacunaDto);
}
