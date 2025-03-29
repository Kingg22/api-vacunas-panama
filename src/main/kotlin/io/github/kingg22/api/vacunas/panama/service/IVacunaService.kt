package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.web.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto
import java.util.UUID

interface IVacunaService {
    fun createDosis(insertDosisDto: InsertDosisDto): ApiContentResponse

    val vacunasFabricante: List<VacunaFabricanteDto>

    fun getDosisByIdPacienteIdVacuna(idPaciente: UUID, idVacuna: UUID): List<DosisDto>
}
