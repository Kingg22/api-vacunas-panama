package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
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
@KonvertTo(RolDto::class)
class Rol(
    @Id
    @Column(name = "id", nullable = false)
    var id: Short? = null,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    var descripcion: String? = null,

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,
)
