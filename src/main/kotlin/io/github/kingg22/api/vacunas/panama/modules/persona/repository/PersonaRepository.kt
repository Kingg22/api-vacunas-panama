package io.github.kingg22.api.vacunas.panama.modules.persona.repository

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class PersonaRepository : PanacheRepositoryBase<Persona, UUID> {
    suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?): Persona? =
        withSession {
            find(
                """
            SELECT p
            FROM Persona p
            WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND
            (:cedula IS NULL OR p.cedula = :cedula) AND
            (:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND
            (:correo IS NULL OR p.correo = :correo)
            """,
                mapOf("cedula" to cedula, "pasaporte" to pasaporte, "correo" to correo),
            ).firstResult().awaitSuspending()
        }

    suspend fun findByUsuarioId(id: UUID): Persona? =
        withSession { find("usuario.id = ?1", id).firstResult().awaitSuspending() }

    suspend fun findByUsuarioUsername(username: String): Persona? = withSession {
        find("usuario.username = ?1", username).firstResult().awaitSuspending()
    }
}
