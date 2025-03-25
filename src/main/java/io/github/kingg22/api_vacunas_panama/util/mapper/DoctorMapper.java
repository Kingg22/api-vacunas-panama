package io.github.kingg22.api_vacunas_panama.util.mapper;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Doctor;
import io.github.kingg22.api_vacunas_panama.web.dto.DoctorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = PersonaMapper.class)
public interface DoctorMapper {

    @Mapping(target = "sede", ignore = true)
    Doctor toEntity(DoctorDto doctorDto);

    DoctorDto toDto(Doctor doctor);

}
