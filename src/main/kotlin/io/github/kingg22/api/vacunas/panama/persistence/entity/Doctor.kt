package io.github.kingg22.api.vacunas.panama.persistence.entity

import io.github.kingg22.api.vacunas.panama.web.dto.DoctorDto
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
import java.time.ZoneOffset

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
        Mapping(target = "direccion", expression = "direccion.toDireccionDto()"),
        Mapping(target = "usuario", expression = "usuario?.toUsuarioDto()"),
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
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Persona(estado = estado, direccion = direccion)
