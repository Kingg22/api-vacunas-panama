package io.github.kingg22.api_vacunas_panama.util.mapper;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Fabricante;
import io.github.kingg22.api_vacunas_panama.web.dto.FabricanteDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {DireccionMapper.class, AccountMapper.class})
public interface FabricanteMapper {

    @Mapping(target = "vacunas", ignore = true)
    Fabricante toEntity(FabricanteDto fabricanteDto);

    FabricanteDto toDto(Fabricante fabricante);

}
