package io.github.kingg22.api.vacunas.panama.modules.doctor.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Entity
@Table(
    name = "doctores",
    indexes = [Index(name = "ix_doctores_idoneidad", columnList = "idoneidad")],
)
@KonvertTo(
    DoctorDto::class,
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
class Doctor @JvmOverloads constructor(
    estado: String,
    direccion: Direccion,

    @Nationalized
    @Column(name = "idoneidad", nullable = false, length = 20)
    @Size(max = 20)
    var idoneidad: String,

    @Nationalized
    @Column(name = "categoria", length = 100)
    @Size(max = 100)
    var categoria: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede")
    var sede: Sede? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Persona(estado = estado, direccion = direccion) {
    constructor(
        persona: Persona,
        idoneidad: String,
        categoria: String? = null,
        sede: Sede? = null,
        createdAt: LocalDateTime = LocalDateTime.now(UTC),
        updatedAt: LocalDateTime? = null,
    ) : this(
        estado = persona.estado,
        direccion = persona.direccion,
        idoneidad = idoneidad,
        categoria = categoria,
        sede = sede,
        createdAt = createdAt,
        updatedAt = updatedAt,
    ) {
        super.applyPersona(persona)
    }
}
