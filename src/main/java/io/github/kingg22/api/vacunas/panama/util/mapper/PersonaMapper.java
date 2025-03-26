package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import io.github.kingg22.api.vacunas.panama.web.dto.PersonaDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {DireccionMapper.class, AccountMapper.class})
public interface PersonaMapper {

    Persona toEntity(PersonaDto personaDto);

    PersonaDto toDto(Persona persona);
}
