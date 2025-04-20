package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UsuarioRepository : JpaRepository<Usuario, UUID> {
    fun findByUsuario(username: String?): Usuario?

    @Query(
        """
            SELECT p.usuario
            FROM Persona p
            WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND
            (:cedula IS NULL OR p.cedula = :cedula) AND
            (:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND
            (:correo IS NULL OR p.correo = :correo)
            """,
    )
    fun findByCedulaOrPasaporteOrCorreo(
        @Param("cedula") cedula: String?,
        @Param("pasaporte") pasaporte: String?,
        @Param("correo") correo: String?,
    ): Usuario?

    @Query(
        """
            SELECT f.usuario
            FROM Fabricante f
            LEFT JOIN Entidad e ON f.id = e.id
            WHERE (:licencia IS NOT NULL OR :correo IS NOT NULL) AND
            (:licencia IS NULL OR f.licencia = :licencia) AND
            (:correo IS NULL OR f.entidad.correo = :correo)
            """,
    )
    fun findByLicenciaOrCorreo(@Param("licencia") licencia: String?, @Param("correo") correo: String?): Usuario?
}
