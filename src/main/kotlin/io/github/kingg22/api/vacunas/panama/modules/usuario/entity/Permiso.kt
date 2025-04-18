package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.PermisoDto
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
@Table(
    name = "permisos",
    indexes = [Index(name = "uq_permisos_nombre", columnList = "nombre", unique = true)],
)
@KonvertTo(PermisoDto::class)
class Permiso @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Short? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    @Size(max = 100)
    var nombre: String,

    @Nationalized
    @Column(name = "descripcion", length = 100)
    @Size(max = 100)
    var descripcion: String? = null,

    @ManyToMany
    @JoinTable(
        name = "roles_permisos",
        joinColumns = [JoinColumn(name = "permiso")],
        inverseJoinColumns = [JoinColumn(name = "rol")],
    )
    @JsonBackReference
    val roles: Set<Rol> = emptySet(),

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "idPermiso")
    val rolesPermisos: Set<RolesPermisos> = emptySet(),
)
