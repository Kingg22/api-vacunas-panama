package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.web.dto.FabricanteDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {DireccionMapper.class, AccountMapper.class})
public interface FabricanteMapper {

    FabricanteDto toDto(Fabricante fabricante);
}
