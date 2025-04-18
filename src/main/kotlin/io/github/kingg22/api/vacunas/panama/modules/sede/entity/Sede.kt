package io.github.kingg22.api.vacunas.panama.modules.sede.entity

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Entity
@Table(
    name = "sedes",
    indexes = [Index(name = "ix_sedes_region_dependencia", columnList = "region")],
)
@KonvertTo(
    SedeDto::class,
    mappings = [
        Mapping(target = "id", source = "id"),
        Mapping(target = "nombre", source = "nombre"),
        Mapping(target = "correo", source = "correo"),
        Mapping(target = "telefono", source = "telefono"),
        Mapping(target = "dependencia", source = "dependencia"),
        Mapping(target = "estado", source = "estado"),
        Mapping(target = "disabled", source = "disabled"),
        Mapping(target = "direccion", source = "direccion"),
    ],
)
class Sede @JvmOverloads constructor(
    nombre: String,
    estado: String,
    direccion: Direccion,

    @Nationalized
    @Column(name = "region", length = 50)
    @Size(max = 50)
    var region: String? = null,

    @OneToMany(mappedBy = "sede")
    val dosis: Set<Dosis> = emptySet(),

    @OneToMany(mappedBy = "sede")
    val doctores: Set<Doctor> = emptySet(),

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Entidad(nombre = nombre, direccion = direccion, estado = estado) {
    constructor(
        entidad: Entidad,
        region: String? = null,
        createdAt: LocalDateTime = LocalDateTime.now(UTC),
        updatedAt: LocalDateTime? = null,
    ) : this(entidad.nombre, entidad.estado, entidad.direccion, region, createdAt = createdAt, updatedAt = updatedAt) {
        super.applyEntidad(entidad)
    }
}
