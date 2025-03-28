@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface PersonaRepository : JpaRepository<Persona, UUID> {
    @Query(
        "SELECT p " + "FROM Persona p " +
            "WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND" +
            "(:cedula IS NULL OR p.cedula = :cedula) AND " +
            "(:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND " +
            "(:correo IS NULL OR p.correo = :correo)",
    )
    fun findByCedulaOrPasaporteOrCorreo(
        @Param("cedula") cedula: String?,
        @Param("pasaporte") pasaporte: String?,
        @Param("correo") correo: String?,
    ): Optional<Persona>

    fun findByUsuario_Id(id: @NotNull UUID): Optional<Persona>

    fun findByUsuario_Username(username: @NotNull String): Optional<Persona>
}
