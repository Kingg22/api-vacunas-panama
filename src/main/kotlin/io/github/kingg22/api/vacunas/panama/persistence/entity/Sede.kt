package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
@Table(
    name = "sedes",
    indexes = [Index(name = "ix_sedes_region_dependencia", columnList = "region")],
)
class Sede(
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

    @OneToMany(mappedBy = "sede")
    val sedesInventarios: Set<SedesInventario> = emptySet(),

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Entidad(nombre = nombre, direccion = direccion, estado = estado)
