package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class UsuarioRepository : PanacheRepositoryBase<Usuario, UUID> {
    suspend fun findByIdOrNull(id: UUID): Usuario? = withSession { findById(id).awaitSuspending() }

    suspend fun findByUsername(username: String?): Usuario? = withSession {
        find("username = ?1", username).firstResult().awaitSuspending()
    }

    suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?): Usuario? =
        withSession {
            find(
                """
                SELECT p.usuario
                FROM Persona p
                WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND
                (:cedula IS NULL OR p.cedula = :cedula) AND
                (:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND
                (:correo IS NULL OR p.correo = :correo)
                """,
                mapOf("cedula" to cedula, "pasaporte" to pasaporte, "correo" to correo),
            ).firstResult().awaitSuspending()
        }

    suspend fun findByLicenciaOrCorreo(licencia: String?, correo: String?): Usuario? = withSession {
        find(
            """
            SELECT f.usuario
            FROM Fabricante f
            LEFT JOIN Entidad e ON f.id = e.id
            WHERE (:licencia IS NOT NULL OR :correo IS NOT NULL) AND
            (:licencia IS NULL OR f.licencia = :licencia) AND
            (:correo IS NULL OR f.entidad.correo = :correo)
            """,
            mapOf("licencia" to licencia, "correo" to correo),
        ).firstResult().awaitSuspending()
    }
}
