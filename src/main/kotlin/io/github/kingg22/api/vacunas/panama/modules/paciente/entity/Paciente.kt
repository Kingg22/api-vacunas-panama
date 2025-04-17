package io.github.kingg22.api.vacunas.panama.modules.paciente.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.NamedNativeQuery
import jakarta.persistence.OneToMany
import jakarta.persistence.SqlResultSetMapping
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity
@Table(
    name = "pacientes",
    indexes = [Index(name = "ix_pacientes_id_temporal", columnList = "identificacion_temporal", unique = true)],
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
@KonvertTo(
    PacienteDto::class,
    mappings = [
        Mapping(target = "id", source = "id"),
        Mapping(target = "cedula", source = "cedula"),
        Mapping(target = "pasaporte", source = "pasaporte"),
        Mapping(target = "nombre", source = "nombre"),
        Mapping(target = "nombre2", source = "nombre2"),
        Mapping(target = "apellido1", source = "apellido1"),
        Mapping(target = "apellido2", source = "apellido2"),
        Mapping(target = "correo", source = "correo"),
        Mapping(target = "telefono", source = "telefono"),
        Mapping(target = "fechaNacimiento", source = "fechaNacimiento"),
        Mapping(target = "edad", source = "edad"),
        Mapping(target = "sexo", source = "sexo"),
        Mapping(target = "estado", source = "estado"),
        Mapping(target = "disabled", source = "disabled"),
        Mapping(target = "direccion", source = "direccion"),
        Mapping(target = "usuario", source = "usuario"),
    ],
)
class Paciente @JvmOverloads constructor(
    estado: String,
    direccion: Direccion,

    @Column(name = "identificacion_temporal")
    @Size(max = 255)
    var identificacionTemporal: String? = null,

    @OneToMany(mappedBy = "paciente")
    val dosis: Set<Dosis> = emptySet(),

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Persona(estado = estado, direccion = direccion) {
    @JvmOverloads
    constructor(
        persona: Persona,
        identificacionTemporal: String? = null,
        dosis: Set<Dosis> = emptySet(),
        createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
        updatedAt: LocalDateTime? = null,
    ) : this(persona.estado, persona.direccion, identificacionTemporal, dosis, createdAt, updatedAt) {
        super.applyPersona(persona)
    }

    companion object {
        @JvmStatic
        @JvmName("builderPaciente")
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder : Persona.Builder() {
        var identificacionTemporal: String? = null
        var dosis: Set<Dosis> = emptySet()
        var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        var updatedAt: LocalDateTime? = null

        fun identificacionTemporal(identificacionTemporal: String?) =
            apply { this.identificacionTemporal = identificacionTemporal }

        fun dosis(dosis: Set<Dosis>) = apply { this.dosis = dosis }
        fun createdAt(createdAt: LocalDateTime) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: LocalDateTime?) = apply { this.updatedAt = updatedAt }

        override fun build(): Paciente {
            val persona = super.build()
            return Paciente(
                persona = persona,
                identificacionTemporal = identificacionTemporal,
                dosis = dosis,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }
}
