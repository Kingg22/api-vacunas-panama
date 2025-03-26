package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia;
import io.github.kingg22.api.vacunas.panama.web.dto.DireccionDto;
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DireccionMapper {
    ProvinciaDto provinciaToDto(Provincia provincia);

    DistritoDto distritoToDto(Distrito distrito);

    @Mapping(target = "sedes", ignore = true)
    @Mapping(target = "pacientes", ignore = true)
    Direccion direccionDtoToEntity(DireccionDto direccionDto);

    DireccionDto direccionToDto(Direccion direccion);

    List<DistritoDto> distritoListToDto(List<Distrito> distritos);

    List<ProvinciaDto> provinciaListToDto(List<Provincia> provinciaList);
}
