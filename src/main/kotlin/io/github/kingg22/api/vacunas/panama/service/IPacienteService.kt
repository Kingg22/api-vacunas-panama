package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto
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
