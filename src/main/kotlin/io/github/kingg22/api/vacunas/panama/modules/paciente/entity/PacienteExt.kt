package io.github.kingg22.api.vacunas.panama.modules.paciente.entity

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.fromPaciente

fun Paciente.toPacienteDto() = PacienteDto.fromPaciente(this)
