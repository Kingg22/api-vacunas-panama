package io.github.kingg22.api.vacunas.panama.modules.fabricante.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
@Table(
    name = "fabricantes",
    indexes = [Index(name = "ix_fabricantes_licencia", columnList = "licencia")],
)
@KonvertTo(
    FabricanteDto::class,
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
class Fabricante @JvmOverloads constructor(
    nombre: String,
    estado: String,
    direccion: Direccion,

    @Nationalized
    @Column(name = "licencia", nullable = false, length = 50)
    @Size(max = 50)
    var licencia: String,

    @Nationalized
    @Column(name = "contacto_nombre", length = 100)
    @Size(max = 100)
    var contactoNombre: String? = null,

    @Column(name = "contacto_correo", length = 254)
    @Size(max = 254)
    var contactoCorreo: String? = null,

    @Column(name = "contacto_telefono", length = 15)
    @Size(max = 15)
    var contactoTelefono: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    var usuario: Usuario? = null,

    @ManyToMany
    @JoinTable(
        name = "fabricantes_vacunas",
        joinColumns = [JoinColumn(name = "fabricante")],
        inverseJoinColumns = [JoinColumn(name = "vacuna")],
    )
    @JsonBackReference
    val vacunas: Set<Vacuna> = emptySet(),

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : Entidad(nombre = nombre, estado = estado, direccion = direccion)
