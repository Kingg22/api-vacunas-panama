package io.github.kingg22.api.vacunas.panama.modules.paciente.service

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import java.util.Optional
import java.util.UUID

interface IPacienteService {
    fun validateCreatePacienteUsuario(pacienteDto: PacienteDto): ApiContentResponse

    fun createPaciente(pacienteDto: PacienteDto): ApiContentResponse

    fun getPacienteDtoById(id: UUID): PacienteDto

    fun getPacienteByUserID(idUser: UUID): Optional<Paciente>

    fun getPacienteById(idPaciente: UUID): Optional<Paciente>

    fun getViewVacunaEnfermedad(id: UUID): List<ViewPacienteVacunaEnfermedadDto>
}
