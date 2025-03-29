package io.github.kingg22.api.vacunas.panama.modules.vacuna.service

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import java.util.UUID

interface IVacunaService {
    fun createDosis(insertDosisDto: InsertDosisDto): ApiContentResponse

    val vacunasFabricante: List<VacunaFabricanteDto>

    fun getDosisByIdPacienteIdVacuna(idPaciente: UUID, idVacuna: UUID): List<DosisDto>
}
