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
    name = "Paciente.getVacunaEnfermedades",
    query = (
        "SELECT [Nombre Vacuna] AS nombre_vacuna," +
            "[Número de dosis] AS numero_dosis, " +
            "[Enfermedades Prevenidas] AS enfermedades_prevenidas, " +
            "[Edad Mínima Recomendada en Días] AS edad_minima," +
            "[Fecha de Aplicación] AS fecha_aplicacion," +
            "[Intervalo Recomendado entre Dosis 1 y 2 en Días] AS intervalo_recomendado," +
            "[Intervalo Real en Días] AS intervalo_real," +
            "Sede AS nombre_sede," +
            "Dependencia AS dependencia_sede," +
            "id," +
            "id_vacuna," +
            "id_sede, " +
            "id_dosis, " +
            "ids_enfermedades " +
            "FROM view_pacientes_vacunas_enfermedades " +
            "WHERE id = :id " +
            "ORDER BY fecha_aplicacion DESC"
        ),
    resultSetMapping = "view_paciente_vacuna_enfermedad",
)
@NamedNativeQuery(
    name = "Paciente.getVacunaPaciente",
    query = (
        "SELECT [Nombre Vacuna] AS nombre_vacuna," +
            "[Número de dosis] AS numero_dosis, " +
            "[Enfermedades Prevenidas] AS enfermedades_prevenidas, " +
            "[Edad Mínima Recomendada en Días] AS edad_minima," +
            "[Fecha de Aplicación] AS fecha_aplicacion," +
            "[Intervalo Recomendado entre Dosis 1 y 2 en Días] AS intervalo_recomendado," +
            "[Intervalo Real en Días] AS intervalo_real," +
            "Sede AS nombre_sede," +
            "Dependencia AS dependencia_sede," +
            "id," +
            "id_vacuna," +
            "id_sede, " +
            "id_dosis, " +
            "ids_enfermedades " +
            "FROM view_pacientes_vacunas_enfermedades " +
            "WHERE id = :id AND id_vacuna = :vacuna " +
            "ORDER BY fecha_aplicacion DESC"
        ),
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
                ColumnResult(name = "enfermedades_prevenidas", type = String::class),
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
                ColumnResult(name = "ids_enfermedades", type = String::class),
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
