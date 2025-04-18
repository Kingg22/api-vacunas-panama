package io.github.kingg22.api.vacunas.panama.modules.paciente.repository

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PacienteRepository : JpaRepository<Paciente, UUID> {
    @Query(name = "Paciente.getVacunas", nativeQuery = true)
    fun findAllFromViewVacunaEnfermedad(
        @Param("id") id: UUID,
        @Param("vacuna") vacuna: UUID? = null,
    ): List<ViewPacienteVacunaEnfermedadDto>

    fun findByIdentificacionTemporal(idTemporal: String?): Paciente?

    fun findByCedula(cedula: String?): Paciente?

    fun findByPasaporte(pasaporte: String?): Paciente?

    fun findByCorreo(correo: String?): Paciente?

    fun findByTelefono(telefono: String?): Paciente?
}
