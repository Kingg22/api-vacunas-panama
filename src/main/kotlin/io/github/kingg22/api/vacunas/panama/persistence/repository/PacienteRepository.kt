@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente
import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface PacienteRepository : JpaRepository<Paciente, UUID> {
    @Query(name = "Paciente.getVacunaEnfermedades", nativeQuery = true)
    fun findAllFromViewVacunaEnfermedad(@Param("id") id: UUID): List<ViewPacienteVacunaEnfermedadDto>

    @Query(name = "Paciente.getVacunaPaciente", nativeQuery = true)
    fun findAllFromViewVacunaEnfermedad(
        @Param("id") id: UUID?,
        @Param("vacuna") vacuna: UUID?,
    ): List<ViewPacienteVacunaEnfermedadDto>

    @Query(
        "SELECT p " + "FROM Paciente p " +
            "WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :idTemporal IS NOT NULL OR " +
            ":correo IS NOT NULL OR:username IS NOT NULL) AND" +
            "(:cedula IS NOT NULL AND p.cedula = :cedula) AND " +
            "(:pasaporte IS NOT NULL AND p.pasaporte = :pasaporte) AND " +
            "(:idTemporal IS NOT NULL AND p.identificacionTemporal = :idTemporal) AND " +
            "(:correo IS NOT NULL AND p.correo = :correo) AND " +
            "(:username IS NOT NULL AND p.usuario.username = :username)",
    )
    fun findByCedulaOrPasaporteOrIdTemporalOrCorreoOrUsername(
        @Param("cedula") cedula: String?,
        @Param("pasaporte") pasaporte: String?,
        @Param("idTemporal") idTemporal: String?,
        @Param("correo") correo: String?,
        @Param("username") username: String?,
    ): Optional<Paciente>

    fun findByIdentificacionTemporal(idTemporal: String?): Optional<Paciente>

    fun findByUsuario_Id(idUser: @NotNull UUID): Optional<Paciente>

    fun findByCedula(cedula: String?): Optional<Paciente>

    fun findByPasaporte(pasaporte: String?): Optional<Paciente>

    fun findByCorreo(correo: String?): Optional<Paciente>

    fun findByTelefono(telefono: String?): Optional<Paciente>
}
