package io.github.kingg22.api.vacunas.panama.persistence.entity

import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto
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
import java.time.LocalDateTime
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
class Paciente(
    estado: String,
    direccion: Direccion,

    @Column(name = "identificacion_temporal")
    @Size(max = 255)
    var identificacionTemporal: String? = null,

    @OneToMany(mappedBy = "paciente")
    val dosis: Set<Dosis> = emptySet(),
) : Persona(estado = estado, direccion = direccion) {
    constructor(
        persona: Persona,
        identificacionTemporal: String? = null,
        dosis: Set<Dosis> = emptySet(),
    ) : this(persona.estado, persona.direccion, identificacionTemporal, dosis) {
        this.apply {
            this.id = persona.id
            this.cedula = persona.cedula
            this.pasaporte = persona.pasaporte
            this.nombre = persona.nombre
            this.nombre2 = persona.nombre2
            this.apellido1 = persona.apellido1
            this.apellido2 = persona.apellido2
            this.correo = persona.correo
            this.telefono = persona.telefono
            this.fechaNacimiento = persona.fechaNacimiento
            this.edad = persona.edad
            this.sexo = persona.sexo
            this.disabled = persona.disabled
            this.usuario = persona.usuario
            this.createdAt = persona.createdAt
            this.updatedAt = persona.updatedAt
        }
    }

    companion object {
        @JvmStatic
        @JvmName("builderPaciente")
        fun builder() = Builder()

        @JvmStatic
        @JvmName("builderPaciente")
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder : Persona.Builder() {
        var identificacionTemporal: String? = null
        var dosis: Set<Dosis> = emptySet()

        fun identificacionTemporal(identificacionTemporal: String?) =
            apply { this.identificacionTemporal = identificacionTemporal }

        fun dosis(dosis: Set<Dosis> = emptySet()) = apply { this.dosis = dosis }

        override fun build(): Paciente {
            val persona = super.build()
            return Paciente(
                persona = persona,
                identificacionTemporal = identificacionTemporal,
                dosis = dosis,
            )
        }
    }
}
