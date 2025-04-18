package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
@Table(name = "roles_permisos")
class RolesPermisos @JvmOverloads constructor(
    @EmbeddedId
    val id: RolesPermisosId? = null,

    @MapsId("idRol")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "rol", nullable = false)
    val idRol: Rol,

    @MapsId("idPermiso")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "permiso", nullable = false)
    val idPermiso: Permiso,

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
)
