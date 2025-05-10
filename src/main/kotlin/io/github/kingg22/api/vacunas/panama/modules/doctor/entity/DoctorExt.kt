package io.github.kingg22.api.vacunas.panama.modules.doctor.entity

import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.fromDoctor

fun Doctor.toDoctorDto() = DoctorDto.fromDoctor(this)
