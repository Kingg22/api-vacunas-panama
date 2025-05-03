package io.github.kingg22.api.vacunas.panama.modules.paciente.repository

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class PacienteRepository : PanacheRepositoryBase<Paciente, UUID> {
    suspend fun findByIdOrNull(id: UUID): Paciente? = withSession { findById(id).awaitSuspending() }

    suspend fun findAllFromViewVacunaEnfermedad(
        id: UUID,
        vacuna: UUID? = null,
    ): List<ViewPacienteVacunaEnfermedadDto> = withSession { session ->
        val query = """
        SELECT
            v.nombre AS nombre_vacuna,
            d.numero_dosis AS numero_dosis,
            v.edad_minima_dias AS edad_minima,
            d.fecha_aplicacion AS fecha_aplicacion,
            (ev.intervalo_fin - ev.intervalo_inicio)::DOUBLE PRECISION AS intervalo_recomendado,
            (EXTRACT(DAY FROM (
                (SELECT MAX(d2.fecha_aplicacion)
                 FROM dosis d2
                 WHERE d2.paciente = p.id
                   AND d2.vacuna = d.vacuna
                   AND d2.numero_dosis > d.numero_dosis)
                - d.fecha_aplicacion
            )))::INT AS intervalo_real,
            ee.nombre AS nombre_sede,
            ee.dependencia AS dependencia_sede,
            p.id AS id,
            v.id AS id_vacuna,
            ee.id AS id_sede,
            d.id AS id_dosis
        FROM pacientes p
        JOIN dosis d ON p.id = d.paciente
        JOIN vacunas v ON d.vacuna = v.id
        LEFT JOIN sedes s ON d.sede = s.id
        LEFT JOIN entidades ee ON s.id = ee.id
        LEFT JOIN esquemas_vacunacion ev ON v.id = ev.vacuna
        WHERE p.id = :id
          AND (:vacuna IS NULL OR v.id = :vacuna)
        GROUP BY p.id, v.nombre, v.edad_minima_dias,
                 ee.nombre, ee.dependencia, ee.id,
                 v.id, d.id, d.vacuna, d.fecha_aplicacion, d.numero_dosis,
                 ev.intervalo_inicio, ev.intervalo_fin
        ORDER BY d.fecha_aplicacion DESC
        """.trimIndent()
        val result = session.createNativeQuery<Array<*>>(query)
            .setParameter("id", id)
            .apply {
                if (vacuna != null) {
                    setParameter("vacuna", vacuna)
                } else {
                    setParameter("vacuna", null)
                }
            }.resultList.awaitSuspending().toList()

        result.map { row: Array<*> ->
            ViewPacienteVacunaEnfermedadDto(
                vacuna = row[0] as String,
                numeroDosis = row[1] as String,
                edadMinRecomendada = row[2] as Short,
                fechaAplicacion = row[3] as LocalDateTime,
                intervaloRecomendadoDosis = row[4] as Double,
                intervaloRealDosis = row[5] as Int,
                sede = row[6] as String,
                dependencia = row[7] as String,
                idPaciente = row[8] as UUID,
                idVacuna = row[9] as UUID,
                idSede = row[10] as UUID,
                idDosis = row[11] as UUID,
            )
        }
    }

    suspend fun findByIdentificacionTemporal(idTemporal: String?): Paciente? = withSession {
        find("identificacionTemporal = ?1", idTemporal).firstResult().awaitSuspending()
    }

    suspend fun findByPersonaCedula(cedula: String?): Paciente? = withSession {
        find("persona.cedula = ?1", cedula).firstResult().awaitSuspending()
    }

    suspend fun findByPersonaPasaporte(pasaporte: String?): Paciente? = withSession {
        find("persona.pasaporte = ?1", pasaporte).firstResult().awaitSuspending()
    }

    suspend fun findByPersonaCorreo(correo: String?): Paciente? = withSession {
        find("persona.correo = ?1", correo).firstResult().awaitSuspending()
    }

    suspend fun findByPersonaTelefono(telefono: String?): Paciente? = withSession {
        find("persona.telefono = ?1", telefono).firstResult().awaitSuspending()
    }
}
