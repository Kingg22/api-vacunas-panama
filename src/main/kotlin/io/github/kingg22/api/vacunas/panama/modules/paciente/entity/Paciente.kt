package io.github.kingg22.api.vacunas.panama.modules.paciente.entity

import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.NamedNativeQuery
import jakarta.persistence.OneToOne
import jakarta.persistence.SqlResultSetMapping
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "pacientes",
    indexes = [
        Index(name = "uq_pacientes_id_temporal", columnList = "identificacion_temporal", unique = true),
    ],
)
@NamedNativeQuery(
    name = "Paciente.getVacunas",
    query = """
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
        LEFT JOIN esquemas_vacunacion ev on v.id = ev.vacuna
        WHERE p.id = :id
          AND (:vacuna IS NULL OR v.id = :vacuna)
        GROUP BY p.id, v.nombre, v.edad_minima_dias,
                 ee.nombre, ee.dependencia, ee.id,
                 v.id, d.id, d.vacuna, d.fecha_aplicacion, d.numero_dosis,
                 ev.intervalo_inicio, ev.intervalo_fin
        ORDER BY d.fecha_aplicacion DESC
    """,
    resultSetMapping = "view_paciente_vacuna_enfermedad",
)
@SqlResultSetMapping(
    name = "view_paciente_vacuna_enfermedad",
    classes = [
        ConstructorResult(
            targetClass = ViewPacienteVacunaEnfermedadDto::class,
            columns = [
                ColumnResult(name = "nombre_vacuna", type = String::class),
                ColumnResult(name = "numero_dosis", type = String::class),
                ColumnResult(name = "edad_minima", type = Short::class),
                ColumnResult(name = "fecha_aplicacion", type = LocalDateTime::class),
                ColumnResult(name = "intervalo_recomendado", type = Double::class),
                ColumnResult(name = "intervalo_real", type = Int::class),
                ColumnResult(name = "nombre_sede", type = String::class),
                ColumnResult(name = "dependencia_sede", type = String::class),
                ColumnResult(name = "id", type = UUID::class),
                ColumnResult(name = "id_vacuna", type = UUID::class),
                ColumnResult(name = "id_sede", type = UUID::class),
                ColumnResult(name = "id_dosis", type = UUID::class),
            ],
        ),
    ],
)
@KonvertTo(PacienteDto::class)
class Paciente(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("gen_random_uuid()")
    @JoinColumn(name = "id", nullable = false)
    var persona: Persona,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 255)
    @Column(name = "identificacion_temporal")
    var identificacionTemporal: String? = null,
) {
    override fun toString(): String = Paciente::class.simpleName +
        ": id=$id, persona=$persona, createdAt=$createdAt, updatedAt=$updatedAt, identificacionTemporal=$identificacionTemporal"

    @KonvertFrom(PacienteModel::class)
    companion object
}
