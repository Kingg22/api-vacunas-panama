package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis;
import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {VacunaMapper.class, DoctorMapper.class, SedeMapper.class, PacienteMapper.class})
public interface DosisMapper {

    DosisDto toDto(Dosis dosis);

    Dosis toEntity(DosisDto dosisDto);

    List<DosisDto> toDtoList(List<Dosis> dosisList);
}
