package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.domain.UsuarioModel
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.Mapping
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "usuarios",
    indexes = [
        Index(name = "ix_usuarios_username", columnList = "username"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uq_usuarios_username", columnNames = ["username"]),
    ],
)
@KonvertFrom(UsuarioModel::class, [Mapping("clave", constant = "\"\"")])
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:NotNull
    @ColumnDefault("true")
    @Column(name = "disabled", nullable = false)
    var disabled: Boolean = true,

    @Column(name = "last_used")
    var lastUsed: LocalDateTime? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @all:Size(max = 50)
    @Column(name = "username", length = 50)
    var username: String? = null,

    @all:Size(max = 100)
    @all:NotNull
    @Column(name = "clave", nullable = false, length = 100)
    var clave: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = [JoinColumn(name = "usuario")],
        inverseJoinColumns = [JoinColumn(name = "rol")],
    )
    var roles: MutableSet<Rol> = mutableSetOf(),

    @OneToOne(mappedBy = "usuario", fetch = FetchType.EAGER)
    var fabricante: Fabricante? = null,

    @OneToOne(mappedBy = "usuario", fetch = FetchType.EAGER)
    var persona: Persona? = null,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<Usuario, UUID>
}
