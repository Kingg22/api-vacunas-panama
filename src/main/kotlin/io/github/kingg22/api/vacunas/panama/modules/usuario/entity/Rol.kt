package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Entity
@Table(
    name = "roles",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_roles_rol", columnNames = ["nombre"]),
    ],
)
class Rol(
    @Id
    @Column(name = "id", nullable = false)
    var id: Short? = null,

    @all:Size(max = 100)
    @Column(name = "descripcion", length = 100)
    var descripcion: String? = null,

    @all:Size(max = 100)
    @all:NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "roles_permisos",
        joinColumns = [JoinColumn(name = "rol")],
        inverseJoinColumns = [JoinColumn(name = "permiso")],
    )
    var permisos: MutableSet<Permiso> = mutableSetOf(),
)
