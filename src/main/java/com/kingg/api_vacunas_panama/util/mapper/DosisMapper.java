package com.kingg.api_vacunas_panama.util.mapper;

import com.kingg.api_vacunas_panama.persistence.entity.Dosis;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {VacunaMapper.class, DoctorMapper.class, SedeMapper.class})
public interface DosisMapper {

    DosisDto toDto(Dosis dosis);

    Dosis toEntity(DosisDto dosisDto);

}
