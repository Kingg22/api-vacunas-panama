package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.mcarle.konvert.api.KonvertTo
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
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity
@Table(
    name = "usuarios",
    indexes = [Index(name = "ix_usuarios_username", columnList = "usuario", unique = true)],
)
@KonvertTo(UsuarioDto::class)
class Usuario @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Nationalized
    @Column(name = "usuario", length = 50)
    @Size(max = 50)
    var username: String? = null,

    @Nationalized
    @Column(name = "clave", nullable = false, length = 100)
    @Size(max = 100)
    var password: String,

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "last_used")
    var lastUsed: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = [JoinColumn(name = "usuario")],
        inverseJoinColumns = [JoinColumn(name = "rol")],
    )
    @JsonManagedReference
    val roles: Set<Rol> = emptySet(),

    @OneToOne(mappedBy = "usuario")
    var fabricante: Fabricante? = null,

    @OneToOne(mappedBy = "usuario")
    var persona: Persona? = null,

    @OneToMany(mappedBy = "idUsuario")
    val usuariosRoles: Set<UsuariosRoles> = emptySet(),
) {
    companion object {
        @JvmStatic
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var id: UUID? = null
        var username: String? = null
        lateinit var password: String
        var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        var updatedAt: LocalDateTime? = null
        var lastUsed: LocalDateTime? = null
        var roles: Set<Rol> = emptySet()
        var fabricante: Fabricante? = null
        var persona: Persona? = null
        var usuariosRoles: Set<UsuariosRoles> = emptySet()

        fun id(id: UUID?) = apply { this.id = id }
        fun username(username: String?) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun createdAt(createdAt: LocalDateTime) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: LocalDateTime?) = apply { this.updatedAt = updatedAt }
        fun lastUsed(lastUsed: LocalDateTime?) = apply { this.lastUsed = lastUsed }
        fun roles(roles: Set<Rol>) = apply { this.roles = roles }
        fun fabricante(fabricante: Fabricante?) = apply { this.fabricante = fabricante }
        fun persona(persona: Persona?) = apply { this.persona = persona }
        fun usuariosRoles(usuariosRoles: Set<UsuariosRoles>) = apply { this.usuariosRoles = usuariosRoles }

        fun build() = Usuario(
            id = id,
            username = username,
            password = password,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastUsed = lastUsed,
            roles = roles,
            fabricante = fabricante,
            persona = persona,
            usuariosRoles = usuariosRoles,
        )
    }
}
